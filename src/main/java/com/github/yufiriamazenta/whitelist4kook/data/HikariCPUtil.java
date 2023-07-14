package com.github.yufiriamazenta.whitelist4kook.data;

import com.github.yufiriamazenta.lib.util.MsgUtil;
import com.github.yufiriamazenta.whitelist4kook.Whitelist4Kook;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import kotlin.random.URandomKt;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPUtil {

    private static HikariDataSource sqlConnectionPool;

    public static void initHikariCP() {
        ConfigurationSection hikari = Whitelist4Kook.getInstance().getConfig().getConfigurationSection("hikariCP");
        if (hikari == null) {
            throw new IllegalArgumentException("The configuration content of HikariCP was not found");
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(hikari.getString("driver", "com.mysql.cj.jdbc.Driver"));

        hikariConfig.setConnectionTimeout(hikari.getInt("connectionTimeout", 3000));
        hikariConfig.setMinimumIdle(hikari.getInt("minimumIdle", 10));
        hikariConfig.setMaxLifetime(hikari.getInt("maxLifeTime", 3000));
        hikariConfig.setMaximumPoolSize(hikari.getInt("maximumPoolSize", 50));
        hikariConfig.setAutoCommit(hikari.getBoolean("autoCommit", true));
        ConfigurationSection mysql = Whitelist4Kook.getInstance().getConfig().getConfigurationSection("mysql");
        if (mysql == null) {
            throw new IllegalArgumentException("The configuration content of mysql was not found");
        }
        String mysqlUrl = "jdbc:mysql://"
                + mysql.getString("address") + ":"
                + mysql.getString("port") + "/"
                + mysql.getString("database")
                + mysql.getString("args", "");
        hikariConfig.setJdbcUrl(mysqlUrl);
        hikariConfig.setUsername(mysql.getString("user"));
        hikariConfig.setPassword(mysql.getString("password"));
        sqlConnectionPool = new HikariDataSource(hikariConfig);
    }


    public static Connection getConn() {
        try {
            return sqlConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
