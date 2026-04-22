package ac.cust.custac.platform.bukkit.scheduler.folia;

import ac.grim.grimac.api.plugin.GrimPlugin;
import ac.cust.custac.platform.api.entity.CustACEntity;
import ac.cust.custac.platform.api.scheduler.EntityScheduler;
import ac.cust.custac.platform.api.scheduler.TaskHandle;
import ac.cust.custac.platform.bukkit.CustACBukkitLoaderPlugin;
import ac.cust.custac.platform.bukkit.entity.BukkitCustACEntity;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoliaEntityScheduler implements EntityScheduler {

    @Override
    public void execute(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delay) {
        ((BukkitCustACEntity) entity).getBukkitEntity().getScheduler().execute(CustACBukkitLoaderPlugin.LOADER, task, retired, delay);
    }

    @Override
    public TaskHandle run(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired) {
        ScheduledTask scheduled = ((BukkitCustACEntity) entity).getBukkitEntity().getScheduler().run(
                CustACBukkitLoaderPlugin.LOADER,
                ignored -> task.run(),
                retired
        );

        return scheduled == null ? null : new FoliaTaskHandle(scheduled);
    }

    @Override
    public TaskHandle runDelayed(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long delayTicks) {
        ScheduledTask scheduled = ((BukkitCustACEntity) entity).getBukkitEntity().getScheduler().runDelayed(
                CustACBukkitLoaderPlugin.LOADER,
                ignored -> task.run(),
                retired,
                delayTicks
        );

        return scheduled == null ? null : new FoliaTaskHandle(scheduled);
    }

    @Override
    public TaskHandle runAtFixedRate(@NotNull CustACEntity entity, @NotNull GrimPlugin plugin, @NotNull Runnable task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
        ScheduledTask scheduled = ((BukkitCustACEntity) entity).getBukkitEntity().getScheduler().runAtFixedRate(
                CustACBukkitLoaderPlugin.LOADER,
                ignored -> task.run(),
                retired,
                initialDelayTicks,
                periodTicks
        );

        return scheduled == null ? null : new FoliaTaskHandle(scheduled);
    }
}
