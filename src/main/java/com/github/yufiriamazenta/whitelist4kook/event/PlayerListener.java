package com.github.yufiriamazenta.whitelist4kook.event;

import com.github.yufiriamazenta.lib.util.MsgUtil;
import com.github.yufiriamazenta.whitelist4kook.Whitelist4Kook;
import com.github.yufiriamazenta.whitelist4kook.WhitelistManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

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
                WhitelistManager.addBindCodeCache(code, uuid);
            }
            String msg = Whitelist4Kook.getInstance().getConfig().getString("lang.bot.bind.hint", "%code%");
            msg = msg.replace("%code%", code);
            msg = MsgUtil.color(msg);
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, msg);
        }
    }

}
