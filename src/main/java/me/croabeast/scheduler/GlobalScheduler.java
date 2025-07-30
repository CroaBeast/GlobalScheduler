package me.croabeast.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Scheduler interface for scheduling tasks in a Bukkit/Spigot/Paper/Folia server.
 * <p>
 * This interface provides methods to schedule tasks on the main thread, region threads, and asynchronously.
 * It also includes methods to check the current thread context and cancel scheduled tasks.
 * <p>
 * Note: Folia and Paper have specific methods for region-based task scheduling.
 */
public interface GlobalScheduler {

    /**
     * <b>Folia</b>: Returns whether the current thread is ticking the global region <br>
     * <b>Paper & Bukkit</b>: Returns {@link org.bukkit.Server#isPrimaryThread}
     */
    boolean isGlobalThread();

    /**
     * @return {@link org.bukkit.Server#isPrimaryThread}
     */
    default boolean isTickThread() {
        return Bukkit.getServer().isPrimaryThread();
    }

    /**
     * <b>Folia & Paper</b>: Returns whether the current thread is ticking a region and that the region
     * being ticked owns the specified entity. Note that this function is the only appropriate method of
     * checking for ownership of an entity, as retrieving the entity's location is undefined unless the
     * entity is owned by the current region
     * <p>
     * <b>Bukkit</b>: returns {@link org.bukkit.Server#isPrimaryThread}
     *
     * @param entity Specified entity
     */
    boolean isEntityThread(Entity entity);

    /**
     * <b>Folia & Paper</b>: Returns whether the current thread is ticking a region and that the region
     * being ticked owns the chunk at the specified world and block position as included in the specified location
     * <p>
     * <b>Bukkit</b>: returns {@link org.bukkit.Server#isPrimaryThread}
     *
     * @param location Specified location, must have a non-null world.
     */
    boolean isRegionThread(Location location);

    /**
     * Schedules a task to be executed on the next tick <br>
     * <b>Folia & Paper</b>: ...on the global region <br>
     * <b>Bukkit</b>: ...on the main thread
     *
     * @param runnable The task to execute
     */
    RunnableTask runTask(Runnable runnable);

    /**
     * Schedules a task to be executed after the specified delay in ticks <br>
     * <b>Folia & Paper</b>: ...on the global region <br>
     * <b>Bukkit</b>: ...on the main thread
     *
     * @param runnable The task to execute
     * @param delay    The delay, in ticks
     */
    RunnableTask runTaskLater(Runnable runnable, long delay);

    /**
     * Schedules a repeating task to be executed after the initial delay with the specified period <br>
     * <b>Folia & Paper</b>: ...on the global region <br>
     * <b>Bukkit</b>: ...on the main thread
     *
     * @param runnable The task to execute
     * @param delay    The initial delay, in ticks.
     * @param period   The period, in ticks.
     */
    RunnableTask runTaskTimer(Runnable runnable, long delay, long period);

    /**
     * Deprecated: use {@link #runTask(Runnable)}
     */
    @Deprecated
    RunnableTask runTask(Plugin plugin, Runnable runnable);

    /**
     * Deprecated: use {@link #runTaskLater(Runnable, long)}
     */
    @Deprecated
    RunnableTask runTaskLater(Plugin plugin, Runnable runnable, long delay);

    /**
     * Deprecated: use {@link #runTaskTimer(Runnable, long, long)}
     */
    @Deprecated
    RunnableTask runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period);

    /**
     * <b>Folia & Paper</b>: Schedules a task to be executed on the region which owns the location on the next tick
     * <p>
     * <b>Bukkit</b>: same as {@link #runTask(Runnable)}
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     */
    default RunnableTask runTask(Location location, Runnable runnable) {
        return runTask(runnable);
    }

    /**
     * <b>Folia & Paper</b>: Schedules a task to be executed on the region which owns the location after the
     * specified delay in ticks
     * <p>
     * <b>Bukkit</b>: same as {@link #runTaskLater(Runnable, long)}
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     * @param delay    The delay, in ticks.
     */
    default RunnableTask runTaskLater(Location location, Runnable runnable, long delay) {
        return runTaskLater(runnable, delay);
    }

    /**
     * <b>Folia & Paper</b>: Schedules a repeating task to be executed on the region which owns the location
     * after the initial delay with the specified period
     * <p>
     * <b>Bukkit</b>: same as {@link #runTaskTimer(Runnable, long, long)}
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     * @param delay    The initial delay, in ticks.
     * @param period   The period, in ticks.
     */
    default RunnableTask runTaskTimer(Location location, Runnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period);
    }

    /**
     * Deprecated: use {@link #execute(Runnable)} or {@link #runTask(Runnable)}
     */
    @Deprecated
    default RunnableTask scheduleSyncDelayedTask(Runnable runnable) {
        return runTask(runnable);
    }

    /**
     * Deprecated: use {@link #runTaskLater(Runnable, long)}
     */
    @Deprecated
    default RunnableTask scheduleSyncDelayedTask(Runnable runnable, long delay) {
        return runTaskLater(runnable, delay);
    }

    /**
     * Deprecated: use {@link #runTaskTimer(Runnable, long, long)}
     */
    @Deprecated
    default RunnableTask scheduleSyncRepeatingTask(Runnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period);
    }

    /**
     * <b>Folia & Paper</b>: Schedules a task to be executed on the region which owns the location
     * of given entity on the next tick
     * <p>
     * <b>Bukkit</b>: same as {@link #runTask(Runnable)}
     *
     * @param entity   The entity whose location the region executing should own
     * @param runnable The task to execute
     */
    default RunnableTask runTask(Entity entity, Runnable runnable) {
        return runTask(runnable);
    }

    /**
     * <b>Folia & Paper</b>: Schedules a task to be executed on the region which owns the location
     * of given entity after the specified delay in ticks
     * <p>
     * <b>Bukkit</b>: same as {@link #runTaskLater(Runnable, long)}
     *
     * @param entity   The entity whose location the region executing should own
     * @param runnable The task to execute
     * @param delay    The delay, in ticks.
     */
    default RunnableTask runTaskLater(Entity entity, Runnable runnable, long delay) {
        return runTaskLater(runnable, delay);
    }

    /**
     * <b>Folia & Paper</b>: Schedules a repeating task to be executed on the region which owns the
     * location of given entity after the initial delay with the specified period
     * <p>
     * <b>Bukkit</b>: same as {@link #runTaskTimer(Runnable, long, long)}
     *
     * @param entity   The entity whose location the region executing should own
     * @param runnable The task to execute
     * @param delay    The initial delay, in ticks.
     * @param period   The period, in ticks.
     */
    default RunnableTask runTaskTimer(Entity entity, Runnable runnable, long delay, long period) {
        return runTaskTimer(runnable, delay, period);
    }

    /**
     * Schedules the specified task to be executed asynchronously immediately
     *
     * @param runnable The task to execute
     * @return The {@link RunnableTask} that represents the scheduled task
     */
    RunnableTask runTaskAsynchronously(Runnable runnable);

    /**
     * Schedules the specified task to be executed asynchronously after the time delay has passed
     *
     * @param runnable The task to execute
     * @param delay    The time delay to pass before the task should be executed
     * @return The {@link RunnableTask} that represents the scheduled task
     */
    RunnableTask runTaskLaterAsynchronously(Runnable runnable, long delay);

    /**
     * Schedules the specified task to be executed asynchronously after the initial delay has passed,
     * and then periodically executed with the specified period
     *
     * @param runnable The task to execute
     * @param delay    The time delay to pass before the first execution of the task, in ticks
     * @param period   The time between task executions after the first execution of the task, in ticks
     * @return The {@link RunnableTask} that represents the scheduled task
     */
    RunnableTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period);

    /**
     * Deprecated: use {@link #runTaskAsynchronously(Runnable)} after creating a scheduler for that plugin.
     */
    @Deprecated
    RunnableTask runTaskAsynchronously(Plugin plugin, Runnable runnable);

    /**
     * Deprecated: use {@link #runTaskLaterAsynchronously(Runnable, long)} after creating a scheduler for that plugin.
     */
    @Deprecated
    RunnableTask runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay);

    /**
     * Deprecated: use {@link #runTaskTimerAsynchronously(Runnable, long, long)} after creating a scheduler for that plugin.
     */
    @Deprecated
    RunnableTask runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period);

    /**
     * Calls a method on the main thread and returns a Future object. This task will be executed
     * by the main(Bukkit)/global(Folia&Paper) server thread.
     * <p>
     * Note: The Future.get() methods must NOT be called from the main thread.
     * <p>
     * Note2: There is at least an average of 10ms latency until the isDone() method returns true.
     *
     * @param task Task to be executed
     */
    default <T> Future<T> callSyncMethod(final Callable<T> task) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        execute(() -> {
            try {
                completableFuture.complete(task.call());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return completableFuture;
    }

    /**
     * Schedules a task to be executed on the global region
     *
     * @param runnable The task to execute
     */
    void execute(Runnable runnable);

    /**
     * Cancels a task with the specified task ID.
     * <p>
     * Note: This method should be used with caution, as it may not work as expected
     * if the task is already running or has already completed.
     *
     * @param taskId The ID of the task to cancel
     */
    void cancel(int taskId);

    /**
     * Schedules a task to be executed on the region which owns the location
     *
     * @param location The location which the region executing should own
     * @param runnable The task to execute
     */
    default void execute(Location location, Runnable runnable) {
        execute(runnable);
    }

    /**
     * Schedules a task to be executed on the region which owns the location of given entity
     *
     * @param entity   The entity which location the region executing should own
     * @param runnable The task to execute
     */
    default void execute(Entity entity, Runnable runnable) {
        execute(runnable);
    }

    /**
     * Attempts to cancel all tasks scheduled by this plugin
     */
    void cancelAll();

    /**
     * Attempts to cancel all tasks scheduled by the specified plugin
     *
     * @param plugin specified plugin
     */
    void cancelAll(Plugin plugin);

    /**
     * Returns the scheduler for the specified plugin.
     * <p>
     * This method is used to obtain a scheduler instance for a specific plugin.
     *
     * @param plugin The plugin for which to get the scheduler
     * @return The scheduler instance for the specified plugin
     */
    @NotNull
    static GlobalScheduler getScheduler(@NotNull Plugin plugin) {
        return SchedulerUtils.getScheduler(Objects.requireNonNull(plugin));
    }

    /**
     * Returns the scheduler for the plugin that provides this class.
     * <p>
     * This method is used to obtain a scheduler instance for the plugin that provides this class.
     *
     * @return The scheduler instance for the plugin that provides this class
     */
    @NotNull
    static GlobalScheduler getScheduler() {
        return getScheduler(JavaPlugin.getProvidingPlugin(GlobalScheduler.class));
    }
}
