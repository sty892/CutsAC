package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CustACBrands implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("ac")
                        .literal("brands", Description.of("Toggle brands for the sender"))
                        .permission("custac.brand")
                        .handler(this::handleBrands)
        );
    }

    private void handleBrands(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        if (sender.isPlayer()) {
            CustACAPI.INSTANCE.getAlertManager().toggleBrands(Objects.requireNonNull(context.sender().getPlatformPlayer()), false);
        } else if (sender.isConsole()) {
            CustACAPI.INSTANCE.getAlertManager().toggleConsoleBrands();
        }
    }
}
