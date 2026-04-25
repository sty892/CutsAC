package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public class CustACVerbose implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("grimac")
                        .literal("verbose")
                        .permission("custac.verbose")
                        .handler(this::handleVerbose)
        );
    }

    private void handleVerbose(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        if (sender.isPlayer()) {
            CustACAPI.INSTANCE.getAlertManager().toggleVerbose(context.sender().getPlatformPlayer(), false);
        } else if (sender.isConsole()) {
            CustACAPI.INSTANCE.getAlertManager().toggleConsoleVerbose();
        }
    }
}
