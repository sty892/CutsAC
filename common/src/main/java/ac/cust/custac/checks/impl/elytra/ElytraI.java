package ac.cust.custac.checks.impl.elytra;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PostPredictionCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

@CheckData(name = "ElytraI", description = "Started gliding in water", experimental = true)
public class ElytraI extends Check implements PostPredictionCheck {
    private boolean setback;

    public ElytraI(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION
                && new WrapperPlayClientEntityAction(event).getAction() == WrapperPlayClientEntityAction.Action.START_FLYING_WITH_ELYTRA
                && player.wasTouchingWater
                && player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_15)
                && flagAndAlert()
        ) {
            setback = true;
            if (shouldModifyPackets()) {
                event.setCancelled(true);
                player.onPacketCancel();
                player.resyncPose();
            }
        }
    }

    @Override
    public void onPredictionComplete(PredictionComplete predictionComplete) {
        if (setback) {
            setbackIfAboveSetbackVL();
            setback = false;
        }
    }
}
