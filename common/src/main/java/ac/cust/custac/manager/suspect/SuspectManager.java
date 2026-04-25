package ac.cust.custac.manager.suspect;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.checks.Check;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.platform.api.player.PlatformPlayer;
import ac.cust.custac.platform.api.sender.Sender;
import ac.cust.custac.utils.math.Location;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class SuspectManager {
    private static final long POST_FLAG_CAPTURE_MS = 30_000L;

    private final AtomicInteger nextFlagId = new AtomicInteger(1);
    private final Map<UUID, SuspectProfile> profiles = new ConcurrentHashMap<>();
    private final Map<String, UUID> names = new ConcurrentHashMap<>();
    private final Map<Integer, SuspectFlag> flagsById = new ConcurrentHashMap<>();
    private volatile SuspectUiHandler uiHandler;

    public void setUiHandler(SuspectUiHandler uiHandler) {
        this.uiHandler = uiHandler;
    }

    public SuspectFlag recordFlag(CustACPlayer player, Check check, String verbose, int violationLevel) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        SuspectProfile profile = profiles.computeIfAbsent(uuid, ignored -> new SuspectProfile(uuid, name));
        profile.setPlayerName(name);
        names.put(name.toLowerCase(Locale.ROOT), uuid);

        LocalDate today = LocalDate.now();
        profile.startTracking(today);

        Location location = safeLocation(player);
        profile.setLastKnownLocation(location);

        List<ReplayFrame> frames = profile.copyRollingFrames();
        SuspectFlag flag = new SuspectFlag(
                nextFlagId.getAndIncrement(),
                uuid,
                name,
                check.getDisplayName(),
                verbose == null ? "" : verbose,
                violationLevel,
                System.currentTimeMillis(),
                SuspectProfile.copyLocation(location),
                frames
        );
        profile.addFlag(flag);
        flagsById.put(flag.id(), flag);
        return flag;
    }

    public void recordLogoutLocation(CustACPlayer player) {
        SuspectProfile profile = profiles.get(player.getUniqueId());
        if (profile != null) {
            profile.setLastKnownLocation(safeLocation(player));
        }
    }

    public void tick() {
        LocalDate today = LocalDate.now();
        for (SuspectProfile profile : profiles.values()) {
            if (!profile.isTracking(today)) {
                profile.stopTracking();
                continue;
            }

            PlatformPlayer platformPlayer = CustACAPI.INSTANCE.getPlatformPlayerFactory().getFromUUID(profile.getUuid());
            if (platformPlayer == null || !platformPlayer.isOnline()) {
                continue;
            }

            CustACPlayer player = CustACAPI.INSTANCE.getPlayerDataManager().getPlayer(profile.getUuid());
            if (player == null) {
                continue;
            }

            Location location = safeLocation(player);
            if (location == null) {
                continue;
            }

            ReplayFrame frame = new ReplayFrame(
                    CustACAPI.INSTANCE.getTickManager().currentTick,
                    System.currentTimeMillis(),
                    SuspectProfile.copyLocation(location),
                    player.onGround,
                    player.isSprinting,
                    player.isSneaking
            );

            profile.setLastKnownLocation(location);
            profile.addFrame(frame);
            appendPostFlagFrames(profile, frame);
        }
    }

    private void appendPostFlagFrames(SuspectProfile profile, ReplayFrame frame) {
        long now = System.currentTimeMillis();
        for (SuspectFlag flag : profile.getFlags()) {
            if (now - flag.createdAt() <= POST_FLAG_CAPTURE_MS) {
                flag.replayFrames().add(frame);
            }
        }
    }

    public boolean openSuspects(Sender sender) {
        SuspectUiHandler handler = uiHandler;
        return handler != null && handler.openSuspects(sender);
    }

    public boolean playReplay(Sender sender, SuspectFlag flag) {
        SuspectUiHandler handler = uiHandler;
        return handler != null && handler.playReplay(sender, flag);
    }

    public List<SuspectProfile> getRankedSuspects() {
        List<SuspectProfile> result = new ArrayList<>(profiles.values());
        result.removeIf(profile -> profile.getFlagCount() <= 0);
        result.sort(Comparator
                .comparingInt(SuspectProfile::getFlagCount).reversed()
                .thenComparing(Comparator.comparingLong(SuspectProfile::getLastFlagTime).reversed()));
        return result;
    }

    public SuspectProfile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public SuspectProfile getProfile(String playerName) {
        if (playerName == null) return null;
        UUID uuid = names.get(playerName.toLowerCase(Locale.ROOT));
        if (uuid != null) {
            return profiles.get(uuid);
        }
        for (SuspectProfile profile : profiles.values()) {
            if (profile.getPlayerName().equalsIgnoreCase(playerName)) {
                return profile;
            }
        }
        return null;
    }

    public SuspectFlag getFlag(int id) {
        return flagsById.get(id);
    }

    public List<String> getKnownNames() {
        return getRankedSuspects().stream().map(SuspectProfile::getPlayerName).toList();
    }

    private Location safeLocation(CustACPlayer player) {
        if (player.platformPlayer == null || player.platformPlayer.getWorld() == null) {
            return null;
        }
        return player.getLocation();
    }
}
