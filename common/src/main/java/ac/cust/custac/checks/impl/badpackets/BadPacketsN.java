package ac.cust.custac.checks.impl.badpackets;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.player.CustACPlayer;

@CheckData(name = "BadPacketsN", setback = 0)
public class BadPacketsN extends Check {
    public BadPacketsN(final CustACPlayer player) {
        super(player);
    }
}
