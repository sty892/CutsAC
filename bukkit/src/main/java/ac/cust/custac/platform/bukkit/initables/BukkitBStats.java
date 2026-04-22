package ac.cust.custac.platform.bukkit.initables;

import ac.cust.custac.manager.init.start.StartableInitable;
import ac.cust.custac.platform.bukkit.CustACBukkitLoaderPlugin;
import ac.cust.custac.utils.anticheat.Constants;
import io.github.retrooper.packetevents.bstats.bukkit.Metrics;

public class BukkitBStats implements StartableInitable {
    @Override
    public void start() {
        try {
            new Metrics(CustACBukkitLoaderPlugin.LOADER, Constants.BSTATS_PLUGIN_ID);
        } catch (Exception ignored) {}
    }
}
