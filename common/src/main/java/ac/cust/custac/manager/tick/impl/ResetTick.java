package ac.cust.custac.manager.tick.impl;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.manager.tick.Tickable;
import ac.cust.custac.player.CustACPlayer;

public class ResetTick implements Tickable {
    @Override
    public void tick() {
        for (CustACPlayer player : CustACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.checkManager.getPacketEntityReplication().tickStartTick();
        }
    }
}
