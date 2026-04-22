package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.manager.violationdatabase.Violation;
import ac.cust.custac.manager.violationdatabase.ViolationDatabaseManager;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.player.OfflinePlatformPlayer;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.anticheat.MessageUtil;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustACHistory implements BuildableCommand {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("custac", "custacac", "ac")
                        .literal("history", "hist", "check")
                        .permission("custac.help")
                        .required("target", StringParser.stringParser(), adapter.onlinePlayerSuggestions())
                        .optional("page", IntegerParser.integerParser())
                        .permission("custac.history")
                        .handler(this::handleHistory)
        );
    }

    private void handleHistory(CommandContext<Sender> context) {
        Sender sender = context.sender();
        String target = context.get("target");
        Integer page = context.getOrDefault("page", 1);

        CustACAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(CustACAPI.INSTANCE.getGrimPlugin(), () -> {
            int entriesPerPage = CustACAPI.INSTANCE.getConfigManager().getConfig().getIntElse("history.entries-per-page", 15);
            String header = "%prefix% &bЛоги для &f%player% (&f%page%&b/&f%maxPages%&b)";
            
            // Custom format for /ac check
            String logFormat = "%prefix% &8[&f%date%&8] &bНарушение &f%check% (x&c%vl%&f) &7%verbose% (&b%timeago% назад&7)";

            OfflinePlatformPlayer targetPlayer = CustACAPI.INSTANCE.getPlatformPlayerFactory().getOfflineFromName(target);

            ViolationDatabaseManager violations = CustACAPI.INSTANCE.getViolationDatabaseManager();
            int logCount = violations.getLogCount(targetPlayer.getUniqueId());
            
            if (logCount == 0) {
                sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, "%prefix% &cЗаписей для этого игрока не найдено.")));
                return;
            }

            List<Violation> logs = violations.getViolations(targetPlayer.getUniqueId(), page, entriesPerPage);
            int maxPages = (int) Math.ceil((float) logCount / entriesPerPage);

            sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, header
                    .replace("%player%", targetPlayer.getName())
                    .replace("%page%", String.valueOf(page))
                    .replace("%maxPages%", String.valueOf(maxPages))
            )));

            for (int i = logs.size() - 1; i >= 0; i--) {
                Violation log = logs.get(i);
                sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, logFormat
                        .replace("%player%", targetPlayer.getName())
                        .replace("%custac_version%", log.custacVersion())
                        .replace("%client_brand%", log.clientBrand())
                        .replace("%client_version%", log.clientVersion())
                        .replace("%server_version%", log.serverVersion())
                        .replace("%check%", log.checkName())
                        .replace("%verbose%", log.verbose())
                        .replace("%vl%", String.valueOf(log.vl()))
                        .replace("%timeago%", getTimeAgo(log.createdAt()))
                        .replace("%date%", dateFormat.format(new Date(log.createdAt())))
                        .replace("%server%", log.server())
                )));
            }
        });
    }

    /**
     * Calculates the time elapsed since a given timestamp in a human-readable format.
     *
     * @param timestamp The timestamp in milliseconds since epoch (e.g., from System.currentTimeMillis()).
     * @return A string representing the time elapsed (e.g., "5д 3ч 10м").
     */
    private String getTimeAgo(long timestamp) {
        // Calculate duration directly from current time and the provided timestamp
        long durationMillis = System.currentTimeMillis() - timestamp;

        // Ensure duration is non-negative, though for "time ago" it should be.
        if (durationMillis < 0) {
            return "0с"; // Or handle as an error/future time
        }

        long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
        durationMillis -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
        durationMillis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        durationMillis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);

        StringBuilder result = new StringBuilder();
        if (days > 0) result.append(days).append("д ");
        if (hours > 0) result.append(hours).append("ч ");
        if (minutes > 0) result.append(minutes).append("м ");
        if (seconds > 0 || result.isEmpty()) result.append(seconds).append("с");

        return result.toString().trim();
    }
}
