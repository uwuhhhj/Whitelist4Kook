package pers.yufiria.whitelist4kook.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Async {
    private Async() {}

    public static <T> void supplyDb(Plugin plugin,
                                    Supplier<T> dbWork,
                                    Consumer<T> onMain,
                                    Consumer<Throwable> onError) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                T res = dbWork.get();
                Bukkit.getScheduler().runTask(plugin, () -> onMain.accept(res));
            } catch (Throwable t) {
                Bukkit.getScheduler().runTask(plugin, () -> onError.accept(t));
            }
        });
    }

    public static void runDb(Plugin plugin,
                             Runnable dbWork,
                             Consumer<Throwable> onError,
                             Runnable onSuccess) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                dbWork.run();
                Bukkit.getScheduler().runTask(plugin, onSuccess);
            } catch (Throwable t) {
                Bukkit.getScheduler().runTask(plugin, () -> onError.accept(t));
            }
        });
    }
}

