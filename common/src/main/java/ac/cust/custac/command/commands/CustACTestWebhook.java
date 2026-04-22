package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.anticheat.LogUtil;
import ac.cust.custac.utils.anticheat.MessageUtil;
import ac.cust.custac.utils.data.webhook.discord.WebhookMessage;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

public class CustACTestWebhook implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("custac", "custacac")
                        .literal("testwebhook")
                        .permission("custac.testwebhook")
                        .handler(this::handleTestWebhook)
        );
    }

    private void handleTestWebhook(@NotNull CommandContext<Sender> context) {
        if (CustACAPI.INSTANCE.getDiscordManager().isDisabled()) {
            context.sender().sendMessage(MessageUtil.miniMessage(CustACAPI.INSTANCE.getConfigManager().getWebhookNotEnabled()));
            return;
        }

        WebhookMessage webhookMessage = new WebhookMessage().content(CustACAPI.INSTANCE.getConfigManager().getWebhookTestMessage());
        CustACAPI.INSTANCE.getDiscordManager().sendWebhookMessage(webhookMessage).whenCompleteAsync(((successful, throwable) -> {
            if (successful == true) {
                context.sender().sendMessage(MessageUtil.miniMessage(CustACAPI.INSTANCE.getConfigManager().getWebhookTestSucceeded()));
                return;
            }

            context.sender().sendMessage(MessageUtil.miniMessage(CustACAPI.INSTANCE.getConfigManager().getWebhookTestFailed()));

            if (throwable != null) {
                LogUtil.error("Exception caught while sending a Discord webhook test alert", throwable);
            }
        }));
    }
}
