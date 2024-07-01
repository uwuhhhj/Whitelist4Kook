package pers.yufiria.whitelist4kook;

import crypticlib.CrypticLib;
import pers.yufiria.whitelist4kook.config.Configs;
import pers.yufiria.whitelist4kook.data.DataManager;
import snw.jkook.entity.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WhitelistManager {

    private static final Map<String, UUID> bindCodeMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> bindCodeTimeStampMap = new ConcurrentHashMap<>();
    private static final Map<UUID, String> reverseBindCodeMap = new ConcurrentHashMap<>();
    private static final Map<UUID, String> playerNameCache = new ConcurrentHashMap<>();

    static {
        CrypticLib.platform().scheduler().runTaskTimer(Whitelist4Kook.getInstance(), () -> {
            long timeStamp = System.currentTimeMillis();
            long timeout = Configs.codeTimeoutSecond.value() * 1000;
            for (String key : bindCodeTimeStampMap.keySet()) {
                if (timeStamp - bindCodeTimeStampMap.get(key) >= timeout) {
                    removeBindCodeCache(key);
                }
            }
        }, 1, 1);
    }

    public static boolean addBind(UUID uuid, User user) {
        return DataManager.addBind(uuid, user.getId());
    }

    public static void removeBind(UUID uuid) {
        DataManager.removeBind(uuid);
    }

    public static void removeBind(User user) {
        DataManager.removeBind(user.getId());
    }

    public static String getBind(UUID uuid) {
        return DataManager.getBind(uuid);
    }

    public static String getBind(User user) {
        return DataManager.getBind(user.getId());
    }

    public static Map<String, UUID> getBindCodeMap() {
        return bindCodeMap;
    }

    public static Map<UUID, String> getReverseBindCodeMap() {
        return reverseBindCodeMap;
    }

    public static String getPlayerNameCache(UUID uuid) {
        return playerNameCache.get(uuid);
    }

    public static void addBindCodeCache(String code, UUID uuid, String name) {
        bindCodeMap.put(code, uuid);
        reverseBindCodeMap.put(uuid, code);
        bindCodeTimeStampMap.put(code, System.currentTimeMillis());
        playerNameCache.put(uuid, name);
    }

    public static void removeBindCodeCache(String code) {
        UUID uuid = bindCodeMap.get(code);
        reverseBindCodeMap.remove(uuid);
        playerNameCache.remove(uuid);
        bindCodeMap.remove(code);
        bindCodeTimeStampMap.remove(code);
    }

}
