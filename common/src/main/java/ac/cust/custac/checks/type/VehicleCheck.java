package ac.cust.custac.checks.type;

import ac.grim.grimac.api.AbstractCheck;
import ac.cust.custac.utils.anticheat.update.VehiclePositionUpdate;

public interface VehicleCheck extends AbstractCheck {

    void process(final VehiclePositionUpdate vehicleUpdate);
}
