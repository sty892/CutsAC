package ac.cust.custac.platform.bukkit.scheduler.bukkit;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.cust.custac.platform.api.entity.CustACEntity;
import ac.cust.custac.platform.api.scheduler.EntityScheduler;
import ac.cust.custac.platform.api.scheduler.TaskHandle;
import ac.cust.custac.platform.bukkit.CustACBukkitLoaderPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitEntityScheduler implements EntityScheduler {
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    @Override
    public void execute(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable run, @Nullable Runnable retired, long delay) {
        scheduler.runTaskLater(CustACBukkitLoaderPlugin.LOADER, run, delay);
    }

    @Override
    public TaskHandle run(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired) {
        return new BukkitTaskHandle(scheduler.runTask(CustACBukkitLoaderPlugin.LOADER, task));
    }

    @Override
    public TaskHandle runDelayed(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delayTicks) {
        return new BukkitTaskHandle(scheduler.runTaskLater(CustACBukkitLoaderPlugin.LOADER, task, delayTicks));
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
        return new BukkitTaskHandle(scheduler.runTaskTimer(CustACBukkitLoaderPlugin.LOADER, task, initialDelayTicks, periodTicks));
    }
}
