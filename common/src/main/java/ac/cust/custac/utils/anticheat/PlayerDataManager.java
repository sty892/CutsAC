package ac.cust.custac.utils.anticheat;

import ac.cust.custac.CustACAPI;
import ac.grim.grimac.api.event.events.GrimJoinEvent;
import ac.grim.grimac.api.event.events.GrimQuitEvent;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.reflection.GeyserUtil;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.netty.channel.ChannelHelper;
import com.github.retrooper.packetevents.protocol.player.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    public final Collection<User> exemptUsers = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<User, CustACPlayer> playerDataMap = new ConcurrentHashMap<>();

    @Nullable
    public CustACPlayer getPlayer(final @NotNull UUID uuid) {
        // Is it safe to interact with this, or is this internal PacketEvents code?
        Object channel = PacketEvents.getAPI().getProtocolManager().getChannel(uuid);
        User user = PacketEvents.getAPI().getProtocolManager().getUser(channel);
        return getPlayer(user);
    }

    @Nullable
    public CustACPlayer getPlayer(final @NotNull User user) {
        @Nullable CustACPlayer player = playerDataMap.get(user);
        if (player != null && player.platformPlayer != null && player.platformPlayer.isExternalPlayer())
            return null;
        return player;
    }

    public boolean shouldCheck(@NotNull User user) {
        if (exemptUsers.contains(user)) return false;
        if (!ChannelHelper.isOpen(user.getChannel())) return false;

        if (user.getUUID() != null) {
            // Bedrock players don't have Java movement
            if (GeyserUtil.isBedrockPlayer(user.getUUID())) {
                exemptUsers.add(user);
                return false;
            }

            // Has exempt permission
            CustACPlayer custacPlayer = CustACAPI.INSTANCE.getPlayerDataManager().getPlayer(user);
            if (custacPlayer != null && custacPlayer.hasPermission("custac.exempt")) {
                exemptUsers.add(user);
                return false;
            }

            // Geyser formatted player string
            // This will never happen for Java players, as the first character in the 3rd group is always 4 (xxxxxxxx-xxxx-4xxx-xxxx-xxxxxxxxxxxx)
            if (user.getUUID().toString().startsWith("00000000-0000-0000-0009")) {
                exemptUsers.add(user);
                return false;
            }
        }

        return true;
    }

    public void addUser(final @NotNull User user) {
        if (shouldCheck(user)) {
            CustACPlayer player = new CustACPlayer(user);
            playerDataMap.put(user, player);
            CustACAPI.INSTANCE.getEventBus().post(new GrimJoinEvent(player));
        }
    }

    public CustACPlayer remove(final @NotNull User user) {
        return playerDataMap.remove(user);
    }

    public void onDisconnect(User user) {
        CustACPlayer custacPlayer = remove(user);
        if (custacPlayer != null) {
            CustACAPI.INSTANCE.getSuspectManager().recordLogoutLocation(custacPlayer);
            CustACAPI.INSTANCE.getEventBus().post(new GrimQuitEvent(custacPlayer));
        }
        exemptUsers.remove(user);

        UUID uuid = user.getProfile().getUUID();

        // Check if calling async is safe
        if (uuid == null)
            return; // folia doesn't like null getPlayer()

        CustACAPI.INSTANCE.getAlertManager().handlePlayerQuit(
                CustACAPI.INSTANCE.getPlatformPlayerFactory().getFromUUID(uuid)
        );

        CustACAPI.INSTANCE.getSpectateManager().onQuit(uuid);

        // TODO (Cross-platform) confirm this is 100% correct and will always remove players from cache when necessary
        CustACAPI.INSTANCE.getPlatformPlayerFactory().invalidatePlayer(uuid);
    }

    public Collection<CustACPlayer> getEntries() {
        return playerDataMap.values();
    }

    public int size() {
        return playerDataMap.size();
    }
}
