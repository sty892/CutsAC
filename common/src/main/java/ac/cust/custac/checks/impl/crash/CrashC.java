package ac.cust.custac.checks.impl.crash;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PacketCheck;
import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;

@CheckData(name = "CrashC", description = "Sent non-finite position or rotation")
public class CrashC extends Check implements PacketCheck {
    public CrashC(CustACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (WrapperPlayClientPlayerFlying.isFlying(event.getPacketType())) {
            WrapperPlayClientPlayerFlying flying = new WrapperPlayClientPlayerFlying(event);
            if (flying.hasPositionChanged()) {
                Location pos = flying.getLocation();
                if (!Double.isFinite(pos.getX()) || !Double.isFinite(pos.getY()) || !Double.isFinite(pos.getZ())
                    || !Float.isFinite(pos.getYaw()) || !Float.isFinite(pos.getPitch())
                   ) {
                    flagAndAlert("xyzYP=" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ", " + pos.getYaw() + ", " + pos.getPitch());
                    player.getSetbackTeleportUtil().executeViolationSetback();
                    event.setCancelled(true);
                    player.onPacketCancel();
                }
            }
        }
    }
}
