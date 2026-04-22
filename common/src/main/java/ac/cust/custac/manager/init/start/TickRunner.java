package ac.cust.custac.manager.init.start;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.platform.api.Platform;
import ac.cust.custac.utils.anticheat.LogUtil;

public class TickRunner implements StartableInitable {
    @Override
    public void start() {
        LogUtil.info("Registering tick schedulers...");

        if (CustACAPI.INSTANCE.getPlatform() == Platform.FOLIA) {
            CustACAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(CustACAPI.INSTANCE.getGrimPlugin(), () -> {
                CustACAPI.INSTANCE.getTickManager().tickSync();
                CustACAPI.INSTANCE.getTickManager().tickAsync();
            }, 1, 1);
        } else {
            CustACAPI.INSTANCE.getScheduler().getGlobalRegionScheduler().runAtFixedRate(CustACAPI.INSTANCE.getGrimPlugin(), () -> CustACAPI.INSTANCE.getTickManager().tickSync(), 0, 1);
            CustACAPI.INSTANCE.getScheduler().getAsyncScheduler().runAtFixedRate(CustACAPI.INSTANCE.getGrimPlugin(), () -> CustACAPI.INSTANCE.getTickManager().tickAsync(), 0, 1);
        }
    }
}
