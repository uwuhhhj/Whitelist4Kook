package pers.yufiria.whitelist4kook.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pers.yufiria.whitelist4kook.config.Configs;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariCPUtil {

    private static HikariDataSource sqlConnectionPool;

    public static void initHikariCP() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Configs.hikariCPDriver.value());
        hikariConfig.setConnectionTimeout(Configs.hikariCPConnectionTimeout.value());
        hikariConfig.setMinimumIdle(Configs.hikariCPMinimumIdle.value());
        hikariConfig.setMaxLifetime(Configs.hikariCPMaxLifeTime.value());
        hikariConfig.setMaximumPoolSize(Configs.hikariCPMaxPoolSize.value());
        hikariConfig.setAutoCommit(Configs.hikariCPAutoCommit.value());
        // Fast-fail settings: short timeouts and minimal idle to avoid long stalls
        try {
            hikariConfig.setValidationTimeout(3000);
            hikariConfig.setInitializationFailTimeout(1_000);
        } catch (Throwable ignored) {
        }
        String mysqlUrl = "jdbc:mysql://"
                + Configs.mysqlAddress.value() + ":"
                + Configs.mysqlPort.value() + "/"
                + Configs.mysqlDatabase.value()
                + Configs.mysqlArgs.value();
        hikariConfig.setJdbcUrl(mysqlUrl);
        hikariConfig.setUsername(Configs.mysqlUser.value());
        hikariConfig.setPassword(Configs.mysqlPassword.value());
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
