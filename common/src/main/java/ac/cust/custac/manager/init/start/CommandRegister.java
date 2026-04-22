package ac.cust.custac.manager.init.start;

import ac.cust.custac.platform.api.command.CommandService;
import ac.cust.custac.utils.anticheat.LogUtil;

public record CommandRegister(CommandService service) implements StartableInitable {

    @Override
    public void start() {
        try {
            if (service != null) {
                service.registerCommands();
            }
        } catch (Throwable t) {
            // This is the ultimate safety net. If command registration fails, CustAC keeps running.
            LogUtil.error("Failed to register commands! CustAC will run without command support.", t);
        }
    }
}
