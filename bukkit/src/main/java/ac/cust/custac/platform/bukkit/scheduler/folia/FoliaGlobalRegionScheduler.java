package ac.cust.custac.platform.bukkit.scheduler.folia;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.cust.custac.platform.api.scheduler.GlobalRegionScheduler;
import ac.cust.custac.platform.api.scheduler.TaskHandle;
import ac.cust.custac.platform.bukkit.CustACBukkitLoaderPlugin;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class FoliaGlobalRegionScheduler implements GlobalRegionScheduler {

    private final io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler globalRegionScheduler = Bukkit.getGlobalRegionScheduler();

    @Override
    public void execute(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
        globalRegionScheduler.execute(CustACBukkitLoaderPlugin.LOADER, task);
    }

    @Override
    public TaskHandle run(@NotNull GrimPlugin plugin, @NotNull Runnable task) {
        return new FoliaTaskHandle(globalRegionScheduler.run(CustACBukkitLoaderPlugin.LOADER, ignored -> task.run()));
    }

    @Override
    public TaskHandle runDelayed(@NotNull GrimPlugin plugin, @NotNull Runnable task, long delay) {
        return new FoliaTaskHandle(globalRegionScheduler.runDelayed(CustACBukkitLoaderPlugin.LOADER, ignored -> task.run(), delay));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull GrimPlugin plugin, @NotNull Runnable task, long initialDelayTicks, long periodTicks) {
        return new FoliaTaskHandle(globalRegionScheduler.runAtFixedRate(CustACBukkitLoaderPlugin.LOADER, ignored -> task.run(), initialDelayTicks, periodTicks));
    }

    @Override
    public void cancel(@NotNull GrimPlugin plugin) {
        globalRegionScheduler.cancelTasks(CustACBukkitLoaderPlugin.LOADER);
    }
}
