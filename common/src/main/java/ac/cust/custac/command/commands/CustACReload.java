package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.anticheat.MessageUtil;
import net.kyori.adventure.text.Component;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public class CustACReload implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("custac", "custacac")
                        .literal("reload")
                        .permission("custac.reload")
                        .handler(this::handleReload)
        );
    }

    private void handleReload(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();

        // reload config
        sender.sendMessage(MessageUtil.getParsedComponent(sender, "reloading", "%prefix% &7Reloading config..."));

        CustACAPI.INSTANCE.getExternalAPI().reloadAsync().exceptionally(throwable -> false)
                .thenAccept(bool -> {
                    Component message = bool
                            ? MessageUtil.getParsedComponent(sender, "reloaded", "%prefix% &fConfig has been reloaded.")
                            : MessageUtil.getParsedComponent(sender, "reload-failed", "%prefix% &cFailed to reload config.");
                    sender.sendMessage(message);
                });
    }
}
