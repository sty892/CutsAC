package ac.cust.custac.manager.init.start;

import ac.cust.custac.CustACAPI;
import ac.cust.custac.command.commands.CustACVersion;

public class UpdateChecker implements StartableInitable {
    @Override
    public void start() {
        if (CustACAPI.INSTANCE.getConfigManager().getConfig().getBooleanElse("check-for-updates", true)) {
            CustACVersion.checkForUpdatesAsync(CustACAPI.INSTANCE.getPlatformServer().getConsoleSender());
        }
    }
}
