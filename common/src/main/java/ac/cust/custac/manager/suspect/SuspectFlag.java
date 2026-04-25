package ac.cust.custac.manager.suspect;

import ac.cust.custac.utils.math.Location;

import java.util.List;
import java.util.UUID;

public record SuspectFlag(
        int id,
        UUID uuid,
        String playerName,
        String checkName,
        String verbose,
        int violationLevel,
        long createdAt,
        Location location,
        List<ReplayFrame> replayFrames
) {
}
