package ac.cust.custac.manager.init.start;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.player.CustACPlayer;

// Intended for future events we inject all platforms at the end of a tick
public abstract class AbstractTickEndEvent implements StartableInitable {

    @Override
    public void start() {

    }

    protected void onEndOfTick(CustACPlayer player) {
        player.checkManager.getPacketEntityReplication().onEndOfTickEvent();
    }

    protected boolean shouldInjectEndTick() {
        return CustACAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("Reach.enable-post-packet", false);
    }
}
