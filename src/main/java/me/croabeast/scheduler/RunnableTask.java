package me.croabeast.scheduler;

import org.bukkit.plugin.Plugin;

/**
 * Represents a task that can be run by the scheduler.
 *
 * <p> This interface provides methods to manage the task's execution state,
 * including cancellation, checking if it's running or repeating, and retrieving
 * the plugin under which the task was scheduled.
 */
public interface RunnableTask {

    /**
     * @return The id of this task.
     */
    int getTaskId();

    /**
     * Cancels executing task
     */
    void cancel();

    /**
     * @return true if task is cancelled, false otherwise
     */
    boolean isCancelled();

    /**
     * @return The plugin under which the task was scheduled.
     */
    Plugin getPlugin();

    /**
     * @return true if task is currently executing, false otherwise
     */
    boolean isRunning();

    /**
     * @return true if task is repeating, false otherwise
     */
    boolean isRepeating();
}