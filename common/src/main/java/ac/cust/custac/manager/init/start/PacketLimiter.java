package ac.cust.custac.manager.init.start;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.player.CustACPlayer;

public class PacketLimiter implements StartableInitable {
    @Override
    public void start() {
        CustACAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(CustACAPI.INSTANCE.getGrimPlugin(), () -> {
            for (CustACPlayer player : CustACAPI.INSTANCE.getPlayerDataManager().getEntries()) {
                // Avoid concurrent reading on an integer as it's results are unknown
                player.cancelledPackets.set(0);
            }
        }, 1, 20);
    }
}
