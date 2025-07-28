package me.croabeast.scheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class FoliaScheduler implements GlobalScheduler {

    private final ConcurrentHashMap<Integer, FoliaTask> tasks = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private final RegionScheduler regionScheduler = Bukkit.getServer().getRegionScheduler();
    private final GlobalRegionScheduler globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();
    private final AsyncScheduler asyncScheduler = Bukkit.getServer().getAsyncScheduler();

    private final Plugin plugin;

    private synchronized int nextId() {
        int taskId;

        tasks.values().removeIf(t -> t == null || t.isFinished());

        do {
            taskId = random.nextInt();
        } while (tasks.containsKey(taskId));

        return taskId;
    }

    @Override
    public boolean isGlobalThread() {
        return Bukkit.getServer().isGlobalTickThread();
    }

    @Override
    public boolean isEntityThread(Entity entity) {
        return Bukkit.getServer().isOwnedByCurrentRegion(entity);
    }

    @Override
    public boolean isRegionThread(Location location) {
        return Bukkit.getServer().isOwnedByCurrentRegion(location);
    }

    @Override
    public RunnableTask runTask(Runnable runnable) {
        return runTask(plugin, runnable);
    }

    @Override
    public RunnableTask runTaskLater(Runnable runnable, long delay) {
        return runTaskLater(plugin, runnable, delay);
    }

    @Override
    public RunnableTask runTaskTimer(Runnable runnable, long delay, long period) {
        return runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public RunnableTask runTask(Plugin plugin, Runnable runnable) {
        return new FoliaTask(globalRegionScheduler.run(plugin, task -> runnable.run()), nextId());
    }

    @Override
    public RunnableTask runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        if (delay <= 0) return runTask(runnable);
        return new FoliaTask(globalRegionScheduler.runDelayed(plugin, task -> runnable.run(), delay), nextId());
    }

    @Override
    public RunnableTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        return new FoliaTask(globalRegionScheduler.runAtFixedRate(plugin, task -> runnable.run(), delay <= 0 ? 1L : delay, period), nextId());
    }

    @Override
    public RunnableTask runTaskAsynchronously(Runnable runnable) {
        return runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public RunnableTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    @Override
    public RunnableTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    @Override
    public RunnableTask runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        return new FoliaTask(asyncScheduler.runNow(plugin, task -> runnable.run()), nextId());
    }

    @Override
    public RunnableTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        return new FoliaTask(asyncScheduler.runDelayed(plugin, task -> runnable.run(), (delay <= 0 ? 1L : delay) * 50L, TimeUnit.MILLISECONDS), nextId());
    }

    @Override
    public RunnableTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        return new FoliaTask(asyncScheduler.runAtFixedRate(plugin, task -> runnable.run(), (delay <= 0 ? 1L : delay) * 50, period * 50, TimeUnit.MILLISECONDS), nextId());
    }

    @Override
    public RunnableTask runTask(Location location, Runnable runnable) {
        return new FoliaTask(regionScheduler.run(plugin, location, task -> runnable.run()), nextId());
    }

    @Override
    public RunnableTask runTaskLater(Location location, Runnable runnable, long delay) {
        if (delay <= 0) return runTask(runnable);
        return new FoliaTask(regionScheduler.runDelayed(plugin, location, task -> runnable.run(), delay), nextId());
    }

    @Override
    public RunnableTask runTaskTimer(Location location, Runnable runnable, long delay, long period) {
        return new FoliaTask(regionScheduler.runAtFixedRate(plugin, location, task -> runnable.run(), delay <= 0 ? 1L : delay, period), nextId());
    }

    @Override
    public RunnableTask runTask(Entity entity, Runnable runnable) {
        return new FoliaTask(entity.getScheduler().run(plugin, task -> runnable.run(), null), nextId());
    }

    @Override
    public RunnableTask runTaskLater(Entity entity, Runnable runnable, long delay) {
        if (delay <= 0) return runTask(entity, runnable);
        return new FoliaTask(entity.getScheduler().runDelayed(plugin, task -> runnable.run(), null, delay), nextId());
    }

    @Override
    public RunnableTask runTaskTimer(Entity entity, Runnable runnable, long delay, long period) {
        return new FoliaTask(entity.getScheduler().runAtFixedRate(plugin, task -> runnable.run(), null, delay <= 0 ? 1L : delay, period), nextId());
    }

    @Override
    public void execute(Runnable runnable) {
        globalRegionScheduler.execute(plugin, runnable);
    }

    @Override
    public void cancel(int taskId) {
        try {
            tasks.get(taskId).cancel();
        } catch (Exception e) {
            // If the task is not found or already cancelled, we can ignore the exception
            // This is a common case when tasks are removed from the map after completion
        }
    }

    @Override
    public void execute(Location location, Runnable runnable) {
        regionScheduler.execute(plugin, location, runnable);
    }

    @Override
    public void execute(Entity entity, Runnable runnable) {
        entity.getScheduler().execute(plugin, runnable, null, 1L);
    }

    @Override
    public void cancelTasks() {
        cancelTasks(plugin);
    }

    @Override
    public void cancelTasks(Plugin plugin) {
        globalRegionScheduler.cancelTasks(plugin);
        asyncScheduler.cancelTasks(plugin);
    }

    private final class FoliaTask implements RunnableTask {

        private final ScheduledTask task;
        @Getter
        private final int taskId;

        private FoliaTask(ScheduledTask task, int taskId) {
            this.task = task;
            this.taskId = taskId;
            tasks.put(taskId, this);
        }

        @Override
        public void cancel() {
            task.cancel();
        }

        public boolean isCancelled() {
            return task.isCancelled();
        }

        @Override
        public Plugin getPlugin() {
            return task.getOwningPlugin();
        }

        @Override
        public boolean isRunning() {
            switch (task.getExecutionState()) {
                case RUNNING: case CANCELLED_RUNNING:
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean isRepeating() {
            return task.isRepeatingTask();
        }

        boolean isFinished() {
            return task.getExecutionState() == ScheduledTask.ExecutionState.FINISHED;
        }
    }
}
