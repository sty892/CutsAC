package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.command.CloudCommandService;
import ac.cust.custac.command.requirements.PlayerSenderRequirement;
import ac.cust.custac.platform.api.command.PlayerSelector;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.player.PlatformPlayer;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.anticheat.MessageUtil;
import com.github.retrooper.packetevents.protocol.player.GameMode;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CustACSpectate implements BuildableCommand {
    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(
                commandManager.commandBuilder("ac")
                        .literal("spectate")
                        .permission("custac.spectate")
                        .required("target", adapter.singlePlayerSelectorParser())
                        .handler(this::handleSpectate)
                        .apply(CloudCommandService.REQUIREMENT_FACTORY.create(PlayerSenderRequirement.PLAYER_SENDER_REQUIREMENT))
        );
    }

    private void handleSpectate(@NotNull CommandContext<Sender> context) {
        Sender sender = context.sender();
        PlayerSelector targetSelectorResults = context.getOrDefault("target", null);
        if (targetSelectorResults == null) return;

        PlatformPlayer targetPlatformPlayer = targetSelectorResults.getSinglePlayer().getPlatformPlayer();

        if (targetPlatformPlayer != null && targetPlatformPlayer.getUniqueId().equals(sender.getUniqueId())) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "cannot-run-on-self", "%prefix% &cYou cannot use this command on yourself!"));
            return;
        }

        if (targetPlatformPlayer != null && targetPlatformPlayer.isExternalPlayer()) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "player-not-this-server", "%prefix% &cThis player isn't on this server!"));
            return;
        }

        @NotNull PlatformPlayer platformPlayer = Objects.requireNonNull(sender.getPlatformPlayer());

        // hide player from tab list
        if (CustACAPI.INSTANCE.getSpectateManager().enable(platformPlayer)) {
            sender.sendMessage(MessageUtil.getParsedComponent(sender, "spectate-return", "<click:run_command:/custac stopspectating><hover:show_text:\"/custac stopspectating\">\n%prefix% &fClick here to return to previous location\n</hover></click>"));
        }

        platformPlayer.setGameMode(GameMode.SPECTATOR);
        platformPlayer.teleportAsync(Objects.requireNonNull(targetPlatformPlayer).getLocation());
    }
}
