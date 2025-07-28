package me.croabeast.scheduler;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@UtilityClass
class SchedulerUtils {

    private boolean exists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static final String PREFIX = "io.papermc.paper.threadedregions.";

    private static final boolean FOLIA_EXISTS = exists(PREFIX + "RegionizedServer"),
            CANVAS_EXISTS = exists("io.canvasmc.canvas.server.ThreadedServer"),
            EXPANDED_SCHEDULING_EXISTS = exists(PREFIX + "scheduler.ScheduledTask");

    @NotNull
    static GlobalScheduler getScheduler(Plugin plugin) {
        if (FOLIA_EXISTS || CANVAS_EXISTS)
            return new FoliaScheduler(plugin);

        if (EXPANDED_SCHEDULING_EXISTS)
            return new FoliaScheduler(plugin) {
                @Override
                public boolean isGlobalThread() {
                    return Bukkit.getServer().isPrimaryThread();
                }
            };

        return new BukkitScheduler(plugin);
    }
}
