package pers.yufiria.whitelist4kook;

import crypticlib.BukkitPlugin;
import org.bukkit.Bukkit;
import pers.yufiria.whitelist4kook.config.Configs;
import pers.yufiria.whitelist4kook.data.DataManager;
import pers.yufiria.whitelist4kook.data.HikariCPUtil;
import pers.yufiria.whitelist4kook.event.KookMessageListener;
import pers.yufiria.whitelist4kook.event.PlayerListener;
import pers.yufiria.whitelist4kook.kook.KookUserBindCommands;

import java.util.logging.Level;

public final class Whitelist4Kook extends BukkitPlugin {
    private static Whitelist4Kook INSTANCE;
    private volatile boolean whitelistEnabled;

    public void enable() {
        INSTANCE = this;
        HikariCPUtil.initHikariCP();
        DataManager.createTable();
        Bukkit.getPluginManager().registerEvents(PlayerListener.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(KookMessageListener.INSTANCE, this);
        this.whitelistEnabled = (Boolean) Configs.enabled.value();
        try {
            KookUserBindCommands.register();
        } catch (Throwable t) {
            this.getLogger().log(Level.WARNING, "KOOK内注册指令失败", t);
        }
    }

    public void disable() {
    }

    public static Whitelist4Kook getInstance() {
        return INSTANCE;
    }

    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean enabled) {
        this.whitelistEnabled = enabled;
        try {
            this.getConfig().set("enabled", enabled);
            this.saveConfig();
        } catch (Throwable ignored) {
        }
    }
}

