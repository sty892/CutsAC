package ac.cust.custac.checks.impl.scaffolding;

import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.BlockPlaceCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.BlockPlace;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;

@CheckData(name = "InvalidPlaceB", description = "Sent impossible block face id")
public class InvalidPlaceB extends BlockPlaceCheck {
    public InvalidPlaceB(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        if (place.getFaceId() == 255 && PacketEvents.getAPI().getServerManager().getVersion().isOlderThanOrEquals(ServerVersion.V_1_8)) {
            return;
        }

        if (place.getFaceId() < 0 || place.getFaceId() > 5) {
            // ban
            if (flagAndAlert("direction=" + place.getFaceId()) && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}
