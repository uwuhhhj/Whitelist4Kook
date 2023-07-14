package com.github.yufiriamazenta.whitelist4kook.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DataManager {


    public static void createTable() {
        String sql = """
                create table if not exists kook_whitelist (
                    kook_id varchar(100),
                    uuid varchar(36),
                    primary key (kook_id)
                ) engine = InnoDB;""";
        try(Connection conn = HikariCPUtil.getConn()) {
            if (conn == null) {
                throw new IllegalArgumentException("Mysql connection error");
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getBind(UUID uuid) {
        String uuidStr = uuid.toString();
        String sql = """
                select kook_id from kook_whitelist where uuid = ?;
                """;
        try(Connection conn = HikariCPUtil.getConn()) {
            if (conn == null) {
                throw new IllegalArgumentException("Mysql connection error");
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuidStr);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return rs.getString("kook_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getBind(String id) {
        String sql = "select uuid from kook_whitelist where kook_id = ?";
        try(Connection conn = HikariCPUtil.getConn()) {
            if (conn == null) {
                throw new IllegalArgumentException("Mysql connection error");
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            } else {
                return rs.getString("uuid");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addBind(UUID uuid, String kook_id) {
        String uuidStr = uuid.toString();
        String sql = """
                insert into kook_whitelist(kook_id, uuid) values(?, ?);
                """;
        try(Connection conn = HikariCPUtil.getConn()) {
            if (conn == null) {
                throw new IllegalArgumentException("Mysql connection error");
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kook_id);
            ps.setString(2, uuidStr);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void removeBind(UUID uuid) {
        String uuidStr = uuid.toString();
        String sql = """
                delete from kook_whitelist where uuid = ?;
                """;
        try(Connection conn = HikariCPUtil.getConn()) {
            if (conn == null) {
                throw new IllegalArgumentException("Mysql connection error");
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, uuidStr);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeBind(String kookId) {
        String sql = """
                delete from kook_whitelist where kook_id = ?;
                """;
        try(Connection conn = HikariCPUtil.getConn()) {
            if (conn == null) {
                throw new IllegalArgumentException("Mysql connection error");
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, kookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
