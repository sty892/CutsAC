package ac.cust.custac.platform.bukkit.manager;

import ac.cust.custac.platform.api.manager.MessagePlaceHolderManager;
import ac.cust.custac.platform.api.player.PlatformPlayer;
import ac.cust.custac.platform.bukkit.player.BukkitPlatformPlayer;
import com.github.retrooper.packetevents.util.reflection.Reflection;
import me.clip.placeholderapi.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitMessagePlaceHolderManager implements MessagePlaceHolderManager {
    public static final boolean hasPlaceholderAPI = Reflection.getClassByNameWithoutException("me.clip.placeholderapi.PlaceholderAPI") != null;

    @Override
    public @NotNull String replacePlaceholders(@Nullable PlatformPlayer player, @NotNull String string) {
        if (!hasPlaceholderAPI) return string;
        return PlaceholderAPI.setPlaceholders(player instanceof BukkitPlatformPlayer bukkitPlatformPlayer ? bukkitPlatformPlayer.getBukkitPlayer() : null, string);
    }
}
