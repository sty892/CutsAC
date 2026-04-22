package ac.cust.custac.checks.impl.baritone;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.impl.aim.processor.AimProcessor;
import ac.cust.custac.checks.type.RotationCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.RotationUpdate;
import ac.cust.custac.utils.data.HeadRotation;
import ac.cust.custac.utils.math.CustACMath;

// This check has been patched by Baritone for a long time, and it also seems to false with cinematic camera now, so it is disabled.
@CheckData(name = "Baritone")
public class Baritone extends Check implements RotationCheck {
    private int verbose;

    public Baritone(CustACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        final HeadRotation from = rotationUpdate.getFrom();
        final HeadRotation to = rotationUpdate.getTo();

        final float deltaPitch = Math.abs(to.pitch() - from.pitch());

        // Baritone works with small degrees, limit to 1 degree to pick up on baritone slightly moving aim to bypass anticheats
        if (rotationUpdate.getDeltaXRot() == 0 && deltaPitch > 0 && deltaPitch < 1 && Math.abs(to.pitch()) != 90.0f) {
            if (rotationUpdate.getProcessor().divisorY < CustACMath.MINIMUM_DIVISOR) {
                verbose++;
                if (verbose > 8) {
                    flagAndAlert("Divisor " + AimProcessor.convertToSensitivity(rotationUpdate.getProcessor().divisorX));
                }
            } else {
                verbose = 0;
            }
        }
    }
}
