package ac.cust.custac.checks.impl.badpackets;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PacketCheck;
import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;

@CheckData(name = "BadPacketsB", description = "Ignored set rotation packet")
public class BadPacketsB extends Check implements PacketCheck {

    public BadPacketsB(final CustACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (isTransaction(event.getPacketType())) {
            player.pendingRotations.removeIf(data -> {
                if (player.getLastTransactionReceived() > data.getTransaction()) {
                    if (!data.isAccepted()) {
                        flagAndAlert();
                    }

                    return true;
                }

                return false;
            });
        }
    }
}
