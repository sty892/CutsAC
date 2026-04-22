package ac.cust.custac.checks.impl.badpackets;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PacketCheck;
import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;

@CheckData(name = "BadPacketsZ", experimental = true)
public class BadPacketsZ extends Check implements PacketCheck {
    private boolean sent;

    public BadPacketsZ(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_TICK_END) {
            sent = false;
        }

        if (event.getPacketType() == PacketType.Play.Client.PLAYER_INPUT) {
            if (sent) {
                flagAndAlert();
            }

            sent = true;
        }
    }
}
