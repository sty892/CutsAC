package ac.cust.custac.checks.impl.sprint;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.PostPredictionCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.PredictionComplete;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;

@CheckData(name = "SprintF", description = "Sprinting while gliding", experimental = true)
public class SprintF extends Check implements PostPredictionCheck {
    public SprintF(CustACPlayer player) {
        super(player);
    }

    @Override
    public void onPredictionComplete(final PredictionComplete predictionComplete) {
        if (player.wasGliding && player.isGliding && player.getClientVersion() == ClientVersion.V_1_21_4) {
            if (player.isSprinting) {
                flagAndAlertWithSetback();
            } else {
                reward();
            }
        }
    }
}
