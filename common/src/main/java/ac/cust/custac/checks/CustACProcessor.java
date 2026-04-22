package ac.cust.custac.checks;

import ac.cust.custac.CustACAPI;
import ac.grim.grimac.api.AbstractProcessor;
import ac.grim.grimac.api.config.ConfigReloadable;
import ac.cust.custac.utils.common.ConfigReloadObserver;

public abstract class CustACProcessor implements AbstractProcessor, ConfigReloadable, ConfigReloadObserver {

    // Not everything has to be a check for it to process packets & be configurable

    @Override
    public void reload() {
        reload(CustACAPI.INSTANCE.getConfigManager().getConfig());
    }

}
