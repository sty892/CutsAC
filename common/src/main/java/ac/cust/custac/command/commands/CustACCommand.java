package ac.cust.custac.command.commands;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.BuildableCommand;
import ac.cust.custac.manager.suspect.SuspectFlag;
import ac.cust.custac.manager.suspect.SuspectManager;
import ac.cust.custac.manager.suspect.SuspectProfile;
import ac.cust.custac.platform.api.manager.cloud.CloudCommandAdapter;
import ac.cust.custac.platform.api.player.PlatformPlayer;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.math.Location;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.standard.IntegerParser;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CustACCommand implements BuildableCommand {
    private static final String PERMISSION = "custac.admin";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void register(CommandManager<Sender> commandManager, CloudCommandAdapter adapter) {
        commandManager.command(commandManager.commandBuilder("ac")
                .permission(PERMISSION)
                .handler(context -> sendHelp(context.sender())));

        commandManager.command(commandManager.commandBuilder("ac")
                .literal("suspects")
                .permission(PERMISSION)
                .handler(context -> handleSuspects(context.sender())));

        commandManager.command(commandManager.commandBuilder("ac")
                .literal("check", "Check")
                .permission(PERMISSION)
                .required("target", StringParser.stringParser(), new SuspectNameSuggestions())
                .handler(this::handleCheck));

        commandManager.command(commandManager.commandBuilder("ac")
                .literal("replay")
                .permission(PERMISSION)
                .required("target", StringParser.stringParser(), new SuspectNameSuggestions())
                .required("id", IntegerParser.integerParser())
                .handler(this::handleReplay));

        commandManager.command(commandManager.commandBuilder("ac")
                .literal("goto")
                .permission(PERMISSION)
                .required("id", IntegerParser.integerParser())
                .handler(this::handleGoto));
    }

    private void sendHelp(Sender sender) {
        sender.sendMessage(Component.text("CustAC commands:", NamedTextColor.AQUA));
        sender.sendMessage(Component.text("/ac suspects", NamedTextColor.GRAY)
                .append(Component.text(" - flagged players menu/list", NamedTextColor.DARK_GRAY)));
        sender.sendMessage(Component.text("/ac check <nick>", NamedTextColor.GRAY)
                .append(Component.text(" - all flags for a player", NamedTextColor.DARK_GRAY)));
        sender.sendMessage(Component.text("/ac replay <nick> <id>", NamedTextColor.GRAY)
                .append(Component.text(" - replay data around a flag", NamedTextColor.DARK_GRAY)));
    }

    private void handleSuspects(Sender sender) {
        SuspectManager manager = CustACAPI.INSTANCE.getSuspectManager();
        if (sender.isPlayer() && manager.openSuspects(sender)) {
            return;
        }

        List<SuspectProfile> suspects = manager.getRankedSuspects();
        if (suspects.isEmpty()) {
            sender.sendMessage(Component.text("No suspects yet.", NamedTextColor.GREEN));
            return;
        }

        sender.sendMessage(Component.text("Suspects:", NamedTextColor.AQUA));
        for (int i = 0; i < Math.min(20, suspects.size()); i++) {
            SuspectProfile profile = suspects.get(i);
            sender.sendMessage(Component.text((i + 1) + ". ", NamedTextColor.DARK_GRAY)
                    .append(Component.text(profile.getPlayerName(), NamedTextColor.WHITE))
                    .append(Component.text(" flags=" + profile.getFlagCount(), NamedTextColor.RED))
                    .clickEvent(ClickEvent.runCommand("/ac check " + profile.getPlayerName()))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to open this suspect flags", NamedTextColor.GRAY))));
        }
    }

    private void handleCheck(CommandContext<Sender> context) {
        Sender sender = context.sender();
        String target = context.get("target");
        SuspectProfile profile = CustACAPI.INSTANCE.getSuspectManager().getProfile(target);
        if (profile == null || profile.getFlagCount() == 0) {
            sender.sendMessage(Component.text("No flags found for " + target + ".", NamedTextColor.RED));
            return;
        }

        sender.sendMessage(Component.text("Flags for " + profile.getPlayerName() + ":", NamedTextColor.AQUA));
        for (SuspectFlag flag : profile.getFlags()) {
            sender.sendMessage(flagLine(flag));
        }
    }

    private void handleReplay(CommandContext<Sender> context) {
        Sender sender = context.sender();
        String target = context.get("target");
        int id = context.get("id");
        SuspectManager manager = CustACAPI.INSTANCE.getSuspectManager();
        SuspectProfile profile = manager.getProfile(target);
        SuspectFlag flag = manager.getFlag(id);

        if (profile == null || flag == null || !flag.uuid().equals(profile.getUuid())) {
            sender.sendMessage(Component.text("Replay flag " + id + " for " + target + " was not found.", NamedTextColor.RED));
            return;
        }

        if (sender.isPlayer() && manager.playReplay(sender, flag)) {
            return;
        }

        sender.sendMessage(Component.text("Replay #" + flag.id() + " for " + flag.playerName(), NamedTextColor.AQUA));
        sender.sendMessage(Component.text("Check: " + flag.checkName(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("Time: " + dateFormat.format(flag.createdAt()), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("Captured frames: " + flag.replayFrames().size(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("Click here to teleport to the flag position.", NamedTextColor.YELLOW)
                .clickEvent(ClickEvent.runCommand("/ac goto " + flag.id()))
                .hoverEvent(HoverEvent.showText(Component.text("Teleport to recorded flag location", NamedTextColor.GRAY))));
    }

    private void handleGoto(CommandContext<Sender> context) {
        Sender sender = context.sender();
        int id = context.get("id");
        SuspectFlag flag = CustACAPI.INSTANCE.getSuspectManager().getFlag(id);
        if (flag == null) {
            sender.sendMessage(Component.text("Flag " + id + " was not found.", NamedTextColor.RED));
            return;
        }
        teleport(sender, flag.location(), "flag #" + flag.id());
    }

    private Component flagLine(SuspectFlag flag) {
        TextComponent.Builder builder = Component.text();
        builder.append(Component.text("#" + flag.id(), NamedTextColor.YELLOW));
        builder.append(Component.text(" [" + dateFormat.format(flag.createdAt()) + "] ", NamedTextColor.DARK_GRAY));
        builder.append(Component.text(flag.checkName(), NamedTextColor.WHITE));
        builder.append(Component.text(" vl=" + flag.violationLevel(), NamedTextColor.RED));
        if (!flag.verbose().isBlank()) {
            builder.append(Component.text(" " + flag.verbose(), NamedTextColor.GRAY));
        }
        builder.clickEvent(ClickEvent.runCommand("/ac goto " + flag.id()));
        builder.hoverEvent(HoverEvent.showText(Component.text("Click to teleport to this flag", NamedTextColor.GRAY)));
        return builder.build();
    }

    private void teleport(Sender sender, Location location, String label) {
        if (!sender.isPlayer()) {
            sender.sendMessage(Component.text("Only a player can teleport.", NamedTextColor.RED));
            return;
        }
        PlatformPlayer player = sender.getPlatformPlayer();
        if (player == null) {
            sender.sendMessage(Component.text("Cannot resolve your player object.", NamedTextColor.RED));
            return;
        }
        if (location == null || location.getWorld() == null || !location.getWorld().isLoaded()) {
            sender.sendMessage(Component.text("No loaded location is available for " + label + ".", NamedTextColor.RED));
            return;
        }
        player.teleportAsync(location);
        sender.sendMessage(Component.text("Teleported to " + label + ".", NamedTextColor.GREEN));
    }

    private static final class SuspectNameSuggestions implements SuggestionProvider<Sender> {
        @Override
        public @NotNull CompletableFuture<? extends @NotNull Iterable<? extends @NotNull Suggestion>> suggestionsFuture(
                @NotNull CommandContext<Sender> context,
                @NotNull CommandInput input
        ) {
            List<Suggestion> suggestions = new ArrayList<>();
            for (String name : CustACAPI.INSTANCE.getSuspectManager().getKnownNames()) {
                suggestions.add(Suggestion.suggestion(name));
            }
            return CompletableFuture.completedFuture(Collections.unmodifiableList(suggestions));
        }
    }
}
