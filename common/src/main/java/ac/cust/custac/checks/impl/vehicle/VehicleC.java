package ac.cust.custac.checks.impl.vehicle;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.player.CustACPlayer;

@CheckData(name = "VehicleC")
public class VehicleC extends Check {
    public VehicleC(CustACPlayer player) {
        super(player);
    }
}
