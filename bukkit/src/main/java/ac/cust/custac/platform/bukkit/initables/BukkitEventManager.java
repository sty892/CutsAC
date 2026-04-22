package ac.cust.custac.platform.bukkit.initables;

import ac.cust.custac.manager.init.start.StartableInitable;
import ac.cust.custac.platform.bukkit.CustACBukkitLoaderPlugin;
import ac.cust.custac.platform.bukkit.events.PistonEvent;
import ac.cust.custac.utils.anticheat.LogUtil;
import org.bukkit.Bukkit;

public class BukkitEventManager implements StartableInitable {
    public void start() {
        LogUtil.info("Registering singular bukkit event... (PistonEvent)");

        Bukkit.getPluginManager().registerEvents(new PistonEvent(), CustACBukkitLoaderPlugin.LOADER);
    }
}
