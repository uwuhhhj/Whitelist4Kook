package pers.yufiria.whitelist4kook.event;

import crypticlib.chat.TextProcessor;
import pers.yufiria.whitelist4kook.Whitelist4Kook;
import pers.yufiria.whitelist4kook.WhitelistManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import pers.yufiria.whitelist4kook.config.Configs;

import java.util.UUID;

public enum PlayerListener implements Listener {

    INSTANCE;

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String bind = WhitelistManager.getBind(uuid);
        if (bind == null) {
            String code;
            if (WhitelistManager.getReverseBindCodeMap().containsKey(uuid)) {
                code = WhitelistManager.getReverseBindCodeMap().get(uuid);
            } else {
                code = UUID.randomUUID().toString();
                code = code.substring(code.length() - 6);
                WhitelistManager.addBindCodeCache(code, uuid, event.getName());
            }

            String msg = Configs.langBotBindHint.value();
            msg = msg.replace("%code%", code);
            msg = TextProcessor.color(msg);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, msg);
        }
    }

}
