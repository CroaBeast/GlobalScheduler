package me.croabeast.scheduler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class BukkitScheduler implements GlobalScheduler {

    @NotNull
    final Plugin plugin;

    @Override
    public boolean isGlobalThread() {
        return Bukkit.getServer().isPrimaryThread();
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return isGlobalThread();
    }

    @Override
    public boolean isRegionThread(Location location) {
        return isGlobalThread();
    }

    @Override
    public RunnableTask runTask(Runnable runnable) {
        return new BukkitTask(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public RunnableTask runTaskLater(Runnable runnable, long delay) {
        return new BukkitTask(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
    }

    @Override
    public RunnableTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new BukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period), true);
    }

    @Override
    public RunnableTask runTask(Plugin plugin, Runnable runnable) {
        return new BukkitTask(Bukkit.getScheduler().runTask(plugin, runnable));
    }

    @Override
    public RunnableTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        return new BukkitTask(Bukkit.getScheduler().runTaskLater(plugin, runnable, delay));
    }

    @Override
    public RunnableTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        return new BukkitTask(Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period), true);
    }

    @Override
    public RunnableTask runTaskAsynchronously(Runnable runnable) {
        return new BukkitTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public RunnableTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new BukkitTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay));
    }

    @Override
    public RunnableTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new BukkitTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period), true);
    }

    @Override
    public RunnableTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        return new BukkitTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }

    @Override
    public RunnableTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        return new BukkitTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay));
    }

    @Override
    public RunnableTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        return new BukkitTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period), true);
    }

    @Override
    public void execute(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
    }

    @Override
    public void cancel(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    @Override
    public void cancelAll() {
        cancelAll(plugin);
    }

    @Override
    public void cancelAll(Plugin plugin) {
        Bukkit.getScheduler().cancelTasks(plugin);
    }

    private static final class BukkitTask implements RunnableTask {

        org.bukkit.scheduler.BukkitTask task;
        @Getter
        boolean repeating;

        public BukkitTask(org.bukkit.scheduler.BukkitTask task) {
            this.task = task;
            this.repeating = false;
        }

        public BukkitTask(org.bukkit.scheduler.BukkitTask task, boolean repeating) {
            this.task = task;
            this.repeating = repeating;
        }

        @Override
        public int getTaskId() {
            return task.getTaskId();
        }

        @Override
        public void cancel() {
            task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }

        @Override
        public Plugin getPlugin() {
            return task.getOwner();
        }

        @Override
        public boolean isRunning() {
            return Bukkit.getServer().getScheduler().isCurrentlyRunning(getTaskId());
        }
    }
}
