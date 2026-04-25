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
import org.incendo.cloud.parser.standard.StringParser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustACCacCheck implements BuildableCommand {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("cac")
                        .literal("check")
                        .permission("custac.history")
                        .required("target", StringParser.stringParser(), adapter.onlinePlayerSuggestions())
                        .handler(this::handleCheck)
        );
    }

    private void handleCheck(CommandContext<Sender> context) {
        Sender sender = context.sender();
        String target = context.get("target");

        CustACAPI.INSTANCE.getScheduler().getAsyncScheduler().runNow(CustACAPI.INSTANCE.getGrimPlugin(), () -> {
            OfflinePlatformPlayer targetPlayer = CustACAPI.INSTANCE.getPlatformPlayerFactory().getOfflineFromName(target);
            ViolationDatabaseManager violations = CustACAPI.INSTANCE.getViolationDatabaseManager();

            // Получаем последние 50 нарушений для анализа
            List<Violation> logs = violations.getViolations(targetPlayer.getUniqueId(), 1, 50);

            if (logs.isEmpty()) {
                sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, "%prefix% &cИгрок &f" + target + " &cчист. Нарушений не найдено.")));
                return;
            }

            sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, "%prefix% &bВсе флаги игрока &f" + targetPlayer.getName() + "&b:")));

            for (Violation log : logs) {
                String message = " &8» &7[&f%timeago% назад&7] &bФлаг: &f%check% &7(VL: &c%vl%&7)\n &8  └ &7Детали: &f%verbose%";
                sender.sendMessage(MessageUtil.miniMessage(MessageUtil.replacePlaceholders(sender, message
                        .replace("%check%", log.checkName())
                        .replace("%vl%", String.valueOf(log.vl()))
                        .replace("%verbose%", log.verbose() != null ? log.verbose() : "Нет данных")
                        .replace("%timeago%", getTimeAgo(log.createdAt()))
                )));
            }
        });
    }

    /**
     * Вычисляет прошедшее время в человекочитаемом формате.
     */
    private String getTimeAgo(long timestamp) {
        long durationMillis = System.currentTimeMillis() - timestamp;

        if (durationMillis < 0) {
            return "0с";
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
