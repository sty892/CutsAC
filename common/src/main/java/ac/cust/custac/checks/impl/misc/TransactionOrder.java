package ac.cust.custac.checks.impl.misc;

import ac.cust.custac.checks.Check;
import ac.cust.custac.checks.CheckData;
import ac.cust.custac.player.CustACPlayer;

@CheckData(name = "TransactionOrder")
public class TransactionOrder extends Check {
    public TransactionOrder(CustACPlayer player) {
        super(player);
    }
}
