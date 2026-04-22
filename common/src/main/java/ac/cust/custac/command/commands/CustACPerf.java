package ac.cust.custac.command.commands;

import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.predictionengine.MovementCheckRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public class CustACPerf {

    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        Command.Builder<Sender> custacCommand = commandManager.commandBuilder("custac", "custacac");

        Command.Builder<Sender> configuredBuilder = custacCommand
                .literal("perf", "performance")
                .permission("custac.performance")
                .handler(this::handlePerformance);

        commandManager.command(configuredBuilder);
    }

    private void handlePerformance(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();

        double millis = MovementCheckRunner.predictionNanos / 1000000;
        double longMillis = MovementCheckRunner.longPredictionNanos / 1000000;

        Component message1 = Component.text()
                .append(Component.text("Milliseconds per prediction (avg. 500): ", NamedTextColor.GRAY))
                .append(Component.text(millis, NamedTextColor.WHITE))
                .build();

        Component message2 = Component.text()
                .append(Component.text("Milliseconds per prediction (avg. 20k): ", NamedTextColor.GRAY))
                .append(Component.text(longMillis, NamedTextColor.WHITE))
                .build();

        sender.sendMessage(message1);
        sender.sendMessage(message2);
    }
}
