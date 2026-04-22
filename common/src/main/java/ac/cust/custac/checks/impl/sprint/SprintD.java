package ac.cust.custac.checks.impl.sprint;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PostPredictionCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;

import static com.github.retrooper.packetevents.protocol.potion.PotionTypes.BLINDNESS;

@CheckData(name = "SprintD", description = "Started sprinting while having blindness", setback = 5, experimental = true)
public class SprintD extends Check implements PostPredictionCheck {
    public boolean startedSprintingBeforeBlind = false;

    public SprintD(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ENTITY_ACTION) {
            if (new WrapperPlayClientEntityAction(event).getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
                startedSprintingBeforeBlind = false;
            }
        }
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (player.compensatedEntities.self.hasPotionEffect(BLINDNESS)) {
            if (player.isSprinting && !startedSprintingBeforeBlind) {
                flagAndAlertWithSetback();
            } else reward();
        }
    }
}
