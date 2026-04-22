package ac.cust.custac.checks.impl.aim;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.checks.type.RotationCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.RotationUpdate;

@CheckData(name = "AimDuplicateLook")
public class AimDuplicateLook extends Check implements RotationCheck {
    private boolean exempt;

    public AimDuplicateLook(CustACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final RotationUpdate rotationUpdate) {
        if (player.packetStateData.lastPacketWasTeleport || player.packetStateData.lastPacketWasOnePointSeventeenDuplicate || player.compensatedEntities.self.getRiding() != null) {
            exempt = true;
            return;
        }

        if (exempt) { // Exempt for a tick on teleport
            exempt = false;
            return;
        }

        if (rotationUpdate.getFrom().equals(rotationUpdate.getTo())) {
            flagAndAlert();
        }
    }
}
