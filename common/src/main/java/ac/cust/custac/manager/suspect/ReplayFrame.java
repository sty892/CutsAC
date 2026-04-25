package ac.cust.custac.manager.suspect;

import ac.cust.custac.utils.math.Location;

public record ReplayFrame(int tick, long capturedAt, Location location, boolean onGround, boolean sprinting, boolean sneaking) {
}
