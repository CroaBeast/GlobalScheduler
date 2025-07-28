# GlobalScheduler

GlobalScheduler is a Java scheduling library designed for Bukkit, Spigot, Paper, Folia, and similar Minecraft server platforms. It provides a unified and modern API for scheduling tasks on the main server thread, asynchronous threads, and region-owned threads (for Folia/Paper). This project is a fork of [UniversalScheduler](https://github.com/Anon8281/UniversalScheduler) with a focus on improving package names, method naming conventions, and overall API clarity and usability.

## Supported Platforms and Java Version

- **Minecraft Server Platforms:**
    - Bukkit
    - Spigot
    - PaperMC
    - Folia (including Canvas support)

- **Java Version:** Compatible with Java 8 and above (commonly used in Minecraft plugin development).

- The scheduler automatically detects and adapts to the running server environment to provide appropriate region-based scheduling functionality where supported (notably in Folia and Paper).

## Features

- Unified `GlobalScheduler` interface to abstract scheduling differences between Bukkit and Folia/Paper.
- Support for synchronous scheduling on the main thread or global region.
- Support for region-based scheduling (Folia/Paper), allowing tasks to run on threads that own specific entities or regions.
- Asynchronous task scheduling with delays and periodic execution.
- Convenience methods for scheduling with location or entity context.
- Task management via `RunnableTask` interface with cancellation, running state, and plugin ownership.
- Backwards-compatible deprecated methods for legacy scheduling calls.

## How to Use

### Getting a Scheduler Instance

Use `GlobalScheduler.getScheduler(plugin)` to obtain the appropriate scheduler for your server environment:

```java
import me.croabeast.scheduler.GlobalScheduler;
import org.bukkit.plugin.Plugin;

public final class MyPlugin extends JavaPlugin {
    
    private static GlobalScheduler staticScheduler;
    private GlobalScheduler scheduler;

    @Override
    public void onEnable() {
        this.scheduler = GlobalScheduler.getScheduler(this);
        staticScheduler = this.scheduler; // Store for static access if needed
    }

    @Override
    public void onDisable() {
        // Cancel any running tasks if necessary
    }
}
```

This will instantiate a `FoliaScheduler` if running on Folia or supported Paper, or `BukkitScheduler` for vanilla Bukkit/Spigot environments.

### Scheduling Tasks

Examples of scheduling tasks:

```java
// Run a task on the next main/global tick
scheduler.runTask(() -> {
    // Your task logic here
});

// Run a task later with delay (in ticks)
scheduler.runTaskLater(() -> {
    // Delayed task logic
}, 20L);

// Run a repeating task at fixed intervals (delay and period in ticks)
scheduler.runTaskTimer(() -> {
    // Repeating task logic
}, 20L, 40L);

// Run asynchronously
scheduler.runTaskAsynchronously(() -> {
    // Async task logic here
});
```

### Region or Entity-based Scheduling (Folia/Paper)

```java
// Run a task on the region owning a location
scheduler.runTask(location, () -> { /* Task for region */ });

// Run task related to an entity's region
scheduler.runTask(entity, () -> { /* Task for entity's region */ });
```

## Implementation Details

- The `GlobalScheduler` interface defines the scheduling contract with methods for synchronous, asynchronous, delayed, and repeating tasks.
- `RunnableTask` represents a scheduled task with methods to query its status or cancel it.
- `BukkitScheduler` is an implementation wrapping Bukkit's native scheduler.
- `FoliaScheduler` integrates with Folia's region-based scheduling API, leveraging the region and global region schedulers.
- `GlobalScheduler` also detects the server environment and returns the optimal scheduler implementation.

## Fork Notice

This project is a fork of [UniversalScheduler](https://github.com/Anon8281/UniversalScheduler). The fork improves:

- Package and class naming clarity
- Method names and signatures for readability and consistency
- Enhanced support for Folia region-based scheduling features
- Additional usability improvements and cleanup

## Requirements

- Java 8 or newer
- Bukkit/Spigot/Paper or Folia-compatible Minecraft server

## Integration in Your Project

1. Add this project as a dependency (e.g., via Maven, Gradle, or by including the JAR).
2. Obtain a `GlobalScheduler` instance through `GlobalScheduler.getScheduler(plugin)`.
3. Use the scheduler API to manage your plugin's task scheduling.
4. Cancel tasks responsibly when disabling your plugin.

If you have any questions or contributions, feel free to open an issue or submit a pull request.