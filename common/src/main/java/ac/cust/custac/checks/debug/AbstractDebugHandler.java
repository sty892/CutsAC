package ac.cust.custac.checks.debug;

import ac.cust.custac.checks.Check;
import ac.cust.custac.player.CustACPlayer;

public abstract class AbstractDebugHandler extends Check {
    public AbstractDebugHandler(CustACPlayer player) {
        super(player);
    }

    public abstract void toggleListener(CustACPlayer player);

    public abstract boolean toggleConsoleOutput();
}
