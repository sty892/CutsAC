package ac.cust.custac.checks.impl.multiactions;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PacketCheck;
import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

@CheckData(name = "MultiActionsD", description = "Closed inventory while moving")
public class MultiActionsD extends Check implements PacketCheck {
    public MultiActionsD(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.CLOSE_WINDOW) return;
        if (player.serverOpenedInventoryThisTick) return;

        String verbose = MultiActionsC.getVerbose(player);
        if (verbose.isEmpty()) return;

        // Don't cancel this packet, because it won't do anything except for making chests
        // look like they are still open (desynced),
        // and it can cause incompatibility issues with plugins
        flagAndAlert(verbose);
    }
}
