package ac.cust.custac.checks.impl.combat;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.player.CustACPlayer;

@CheckData(name = "Hitboxes", setback = 10)
public class Hitboxes extends Check {
    public Hitboxes(CustACPlayer player) {
        super(player);
    }
}
