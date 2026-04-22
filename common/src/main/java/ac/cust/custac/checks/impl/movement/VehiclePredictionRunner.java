package ac.cust.custac.checks.impl.movement;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.type.VehicleCheck;
import ac.cust.custac.player.CustACPlayer;
import ac.cust.custac.utils.anticheat.update.PositionUpdate;
import ac.cust.custac.utils.anticheat.update.VehiclePositionUpdate;

public class VehiclePredictionRunner extends Check implements VehicleCheck {
    public VehiclePredictionRunner(CustACPlayer playerData) {
        super(playerData);
    }

    @Override
    public void process(final VehiclePositionUpdate vehicleUpdate) {
        // Vehicle onGround = false always
        // We don't do vehicle setbacks because vehicle netcode sucks.
        player.movementCheckRunner.processAndCheckMovementPacket(new PositionUpdate(vehicleUpdate.from(), vehicleUpdate.to(), false, null, null, vehicleUpdate.isTeleport()));
    }
}
