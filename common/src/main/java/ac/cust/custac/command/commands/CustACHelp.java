package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.anticheat.MessageUtil;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.description.Description;
import org.jetbrains.annotations.NotNull;

public class CustACHelp implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("ac")
                        .literal("help", Description.of("Display help information"))
                        .permission("custac.help")
                        .handler(this::handleHelp)
        );
    }

    private void handleHelp(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();

        for (String string : CustACAPI.INSTANCE.getConfigManager().getConfig().getStringList("help")) {
            if (string == null) continue;
            string = MessageUtil.replacePlaceholders(sender, string);
            sender.sendMessage(MessageUtil.miniMessage(string));
        }
    }
}
