package ac.cust.custac.manager;

import ac.cust.custac.CustACAPI;
import ac.grim.grimac.api.AbstractCheck;
import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.api.config.ConfigReloadable;
import ac.grim.grimac.api.event.events.CommandExecuteEvent;
import ac.cust.custac.checks.Check;
import ac.cust.custac.events.packets.ProxyAlertMessenger;
import ac.cust.custac.platform.api.player.PlatformPlayer;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.LogUtil;
import ac.cust.custac.utils.anticheat.MessageUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PunishmentManager implements ConfigReloadable {
    private final CustACPlayer player;
    private final List<PunishGroup> groups = new ArrayList<>();
    private String experimentalSymbol = "*";
    private String alertString;
    private boolean testMode;
    private String proxyAlertString = "";

    public PunishmentManager(CustACPlayer player) {
        this.player = player;
    }

    @Override
    public void reload(ConfigManager config) {
        List<String> punish = config.getStringListElse("Punishments", new ArrayList<>());
        experimentalSymbol = config.getStringElse("experimental-symbol", "*");
        alertString = config.getStringElse("alerts-format", "%prefix% &f%player% &bfailed &f%check_name% &f(x&c%vl%&f) &7%verbose%");
        testMode = config.getBooleanElse("test-mode", false);
        proxyAlertString = config.getStringElse("alerts-format-proxy", "%prefix% &f[&cproxy&f] &f%player% &bfailed &f%check_name% &f(x&c%vl%&f) &7%verbose%");
        try {
            groups.clear();

            // To support reloading
            for (AbstractCheck check : player.checkManager.allChecks.values()) {
                check.setEnabled(false);
            }

            for (Object s : punish) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) s;

                List<String> checks = (List<String>) map.getOrDefault("checks", new ArrayList<>());
                List<String> commands = (List<String>) map.getOrDefault("commands", new ArrayList<>());
                int removeViolationsAfter = (int) map.getOrDefault("remove-violations-after", 300);

                List<ParsedCommand> parsed = new ArrayList<>();
                List<AbstractCheck> checksList = new ArrayList<>();
                List<AbstractCheck> excluded = new ArrayList<>();
                for (String command : checks) {
                    command = command.toLowerCase(Locale.ROOT);
                    boolean exclude = false;
                    if (command.startsWith("!")) {
                        exclude = true;
                        command = command.substring(1);
                    }
                    for (AbstractCheck check : player.checkManager.allChecks.values()) { // o(n) * o(n)?
                        if (check.getCheckName() != null &&
                                (check.getCheckName().toLowerCase(Locale.ROOT).contains(command)
                                        || check.getAlternativeName().toLowerCase(Locale.ROOT).contains(command))) { // Some checks have equivalent names like AntiKB and AntiKnockback
                            if (exclude) {
                                excluded.add(check);
                            } else {
                                checksList.add(check);
                                check.setEnabled(true);
                            }
                        }
                    }
                    for (AbstractCheck check : excluded) checksList.remove(check);
                }

                for (String command : commands) {
                    String firstNum = command.substring(0, command.indexOf(":"));
                    String secondNum = command.substring(command.indexOf(":"), command.indexOf(" "));

                    int threshold = Integer.parseInt(firstNum);
                    int interval = Integer.parseInt(secondNum.substring(1));
                    String commandString = command.substring(command.indexOf(" ") + 1);

                    parsed.add(new ParsedCommand(threshold, interval, commandString));
                }

                groups.add(new PunishGroup(checksList, parsed, removeViolationsAfter * 1000));
            }
        } catch (Exception e) {
            LogUtil.error("Error while loading punishments.yml! This is likely your fault!", e);
        }
    }

    private String replaceAlertPlaceholders(String original, int vl, Check check, String verbose) {
        return MessageUtil.replacePlaceholders(player, original
                .replace("[alert]", alertString)
                .replace("[proxy]", proxyAlertString)
                .replace("%check_name%", check.getDisplayName())
                .replace("%experimental%", check.isExperimental() ? experimentalSymbol : "")
                .replace("%vl%", Integer.toString(vl))
                .replace("%description%", check.getDescription())
        ).replace("%verbose%", MiniMessage.miniMessage().escapeTags(verbose));
    }

    public boolean handleAlert(CustACPlayer player, String verbose, Check check) {
        boolean sentDebug = false;
        
        // Log every single alert to the database automatically for /cac check
        // This ensures fly, speed, airjump etc. are visible even if not in punishments.yml
        String verboseWithoutGl = verbose.replaceAll(" /gl .*", "");
        CustACAPI.INSTANCE.getViolationDatabaseManager().logAlert(player, verboseWithoutGl, check.getDisplayName(), getViolationsForCheck(check));

        // Check commands from punishments.yml
        for (PunishGroup group : groups) {
            if (group.checks.contains(check)) {
                final int vl = getViolations(group, check);
                final int violationCount = group.violations.size();
                for (ParsedCommand command : group.commands) {
                    String cmd = replaceAlertPlaceholders(command.command, vl, check, verbose);

                    @Nullable Set<@Nullable PlatformPlayer> verboseListeners = null;

                    // Verbose that prints all flags
                    if (CustACAPI.INSTANCE.getAlertManager().hasVerboseListeners() && command.command.equals("[alert]")) {
                        sentDebug = true;
                        Component component = MessageUtil.miniMessage(cmd);
                        verboseListeners = CustACAPI.INSTANCE.getAlertManager().sendVerbose(component, null);
                    }

                    if (violationCount >= command.threshold) {
                        // 0 means execute once
                        // Any other number means execute every X interval
                        boolean inInterval = command.interval == 0 ? (command.executeCount == 0) : (violationCount % command.interval == 0);
                        if (inInterval) {
                            CommandExecuteEvent executeEvent = new CommandExecuteEvent(player, check, verbose, cmd);
                            CustACAPI.INSTANCE.getEventBus().post(executeEvent);
                            if (executeEvent.isCancelled()) continue;

                            switch (command.command) {
                                case "[webhook]" -> CustACAPI.INSTANCE.getDiscordManager().sendAlert(player, verbose, check.getDisplayName(), vl);
                                case "[log]" -> {
                                    // Already logged above automatically
                                }
                                case "[proxy]" -> ProxyAlertMessenger.sendPluginMessage(cmd);
                                case "[alert]" -> {
                                    sentDebug = true;
                                    Component message = MessageUtil.miniMessage(cmd);
                                    if (testMode) { // secret test mode
                                        if (verboseListeners == null || verboseListeners.contains(player.platformPlayer)) {
                                            player.sendMessage(message);
                                        }
                                    } else {
                                        CustACAPI.INSTANCE.getAlertManager().sendAlert(message, verboseListeners);
                                    }
                                }
                                default -> CustACAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().run(CustACAPI.INSTANCE.getGrimPlugin(), () ->
                                        CustACAPI.INSTANCE.getPlatformServer().dispatchCommand(
                                                CustACAPI.INSTANCE.getPlatformServer().getConsoleSender(),
                                                cmd
                                        )
                                );
                            }
                        }

                        command.executeCount++;
                    }
                }
            }
        }

        return sentDebug;
    }

    private int getViolationsForCheck(Check check) {
        int totalVl = 0;
        for (PunishGroup group : groups) {
            if (group.checks.contains(check)) {
                totalVl += getViolations(group, check);
            }
        }
        return totalVl;
    }

    public void handleViolation(Check check) {
        for (PunishGroup group : groups) {
            if (group.checks.contains(check)) {
                long currentTime = System.currentTimeMillis();

                group.violations.put(currentTime, check);
                // Remove violations older than the defined time in the config
                group.violations.long2ObjectEntrySet().removeIf(time -> currentTime - time.getLongKey() > group.removeViolationsAfter);
            }
        }
    }

    private int getViolations(PunishGroup group, Check check) {
        int vl = 0;
        for (Check value : group.violations.values()) {
            if (value == check) vl++;
        }
        return vl;
    }
}

@RequiredArgsConstructor
class PunishGroup {
    public final List<AbstractCheck> checks;
    public final List<ParsedCommand> commands;
    public final Long2ObjectMap<Check> violations = new Long2ObjectOpenHashMap<>();
    public final int removeViolationsAfter; // time to remove violations after in milliseconds
}

@RequiredArgsConstructor
class ParsedCommand {
    public final int threshold;
    public final int interval;
    public final String command;
    public int executeCount;
}
