package ac.cust.custac.checks.impl.badpackets;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.player.CustACPlayer;

@CheckData(name = "BadPacketsW", description = "Interacted with non-existent entity", experimental = true)
public class BadPacketsW extends Check {
    public BadPacketsW(CustACPlayer player) {
        super(player);
    }
}
