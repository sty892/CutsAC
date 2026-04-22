package ac.cust.custac.manager.tick.impl;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.manager.tick.Tickable;
import ac.cust.custac.player.CustACPlayer;

public class ClearRecentlyUpdatedBlocks implements Tickable {

    private static final int maxTickAge = 2;

    @Override
    public void tick() {
        for (CustACPlayer player : CustACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
            player.blockHistory.cleanup(CustACAPI.INSTANCE.getTickManager().currentTick - maxTickAge);
        }
    }
}
