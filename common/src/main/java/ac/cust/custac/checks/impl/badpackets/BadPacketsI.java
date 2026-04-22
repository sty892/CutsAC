package ac.cust.custac.checks.impl.badpackets;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PacketCheck;
import ac.cust.custac.player.CustACPlayer;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerAbilities;

@CheckData(name = "BadPacketsI", description = "Claimed to be flying while unable to fly")
public class BadPacketsI extends Check implements PacketCheck {
    public BadPacketsI(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.PLAYER_ABILITIES
                && new WrapperPlayClientPlayerAbilities(event).isFlying() && !player.canFly
                && flagAndAlert() && shouldModifyPackets()) {
            event.setCancelled(true);
            player.onPacketCancel();
        }
    }
}
