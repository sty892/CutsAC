package ac.cust.custac.manager.suspect;

import ac.cust.custac.utils.math.Location;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class SuspectProfile {
    private static final int PRE_FLAG_FRAMES = 20 * 30;

    private final UUID uuid;
    private String playerName;
    private final List<SuspectFlag> flags = new ArrayList<>();
    private final ArrayDeque<ReplayFrame> rollingFrames = new ArrayDeque<>(PRE_FLAG_FRAMES);
    private Location lastKnownLocation;
    private boolean tracking;
    private LocalDate trackingDate;

    SuspectProfile(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public synchronized UUID getUuid() {
        return uuid;
    }

    public synchronized String getPlayerName() {
        return playerName;
    }

    synchronized void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public synchronized int getFlagCount() {
        return flags.size();
    }

    public synchronized long getLastFlagTime() {
        return flags.isEmpty() ? 0L : flags.get(flags.size() - 1).createdAt();
    }

    public synchronized List<SuspectFlag> getFlags() {
        return Collections.unmodifiableList(new ArrayList<>(flags));
    }

    public synchronized List<SuspectFlag> getLastFlags(int amount) {
        int from = Math.max(0, flags.size() - amount);
        return Collections.unmodifiableList(new ArrayList<>(flags.subList(from, flags.size())));
    }

    synchronized void addFlag(SuspectFlag flag) {
        flags.add(flag);
    }

    synchronized List<ReplayFrame> copyRollingFrames() {
        return new ArrayList<>(rollingFrames);
    }

    synchronized void addFrame(ReplayFrame frame) {
        rollingFrames.addLast(frame);
        while (rollingFrames.size() > PRE_FLAG_FRAMES) {
            rollingFrames.removeFirst();
        }
    }

    public synchronized Location getLastKnownLocation() {
        return copyLocation(lastKnownLocation);
    }

    synchronized void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = copyLocation(lastKnownLocation);
    }

    synchronized void startTracking(LocalDate date) {
        tracking = true;
        trackingDate = date;
    }

    synchronized void stopTracking() {
        tracking = false;
        rollingFrames.clear();
    }

    synchronized boolean isTracking(LocalDate today) {
        return tracking && today.equals(trackingDate);
    }

    static Location copyLocation(Location location) {
        if (location == null) return null;
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
