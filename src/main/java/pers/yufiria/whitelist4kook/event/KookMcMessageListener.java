package pers.yufiria.whitelist4kook.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pers.yufiria.kookmc.event.kook.channel.KookChannelMessageEvent;
import pers.yufiria.whitelist4kook.Whitelist4Kook;
import pers.yufiria.whitelist4kook.config.Configs;
import snw.jkook.JKook;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.ChannelMessage;
import snw.jkook.message.component.BaseComponent;

/**
 * 使用 KookMC 封装的 Bukkit 事件监听频道消息，并仅解析机器人发送的内容。
 */
public enum KookMcMessageListener implements Listener {

    INSTANCE;

    @EventHandler
    public void onKookChannelMessage(KookChannelMessageEvent event) {
        try {
            ChannelMessage msg = event.getMessage();
            TextChannel channel = event.getChannel();
            Guild guild = channel.getGuild();
            User sender = msg.getSender();

            if (sender == null) return;

            // 只处理“本机器人”发送的消息；如果获取不到自身用户，则退化为处理任意机器人消息
            boolean fromAnyBot = sender.isBot();
            boolean fromSelf = false;
            try {
                if (JKook.getCore() != null && JKook.getCore().getUser() != null) {
                    fromSelf = sender.getId().equals(JKook.getCore().getUser().getId());
                }
            } catch (Throwable ignored) { }
            if (!(fromSelf || (!fromSelf && fromAnyBot))) {
                return;
            }

            // 可选：限制处理的服务器/频道
            try {
                var guilds = Configs.bindGuilds.value();
                var channels = Configs.bindChannels.value();
                boolean restrictGuilds = guilds != null && !guilds.isEmpty() && !(guilds.size() == 1 && guilds.contains("0"));
                boolean restrictChannels = channels != null && !channels.isEmpty() && !(channels.size() == 1 && channels.contains("0"));
                if (restrictGuilds && !guilds.contains(guild.getId())) {
                    return;
                }
                if (restrictChannels && !channels.contains(channel.getId())) {
                    return;
                }
            } catch (Throwable ignored) { }

            BaseComponent comp = msg.getComponent();
            String content = comp == null ? "" : comp.toString();
            long ts = msg.getTimeStamp();

            Whitelist4Kook.getInstance().getLogger().info(String.format(
                    "[KOOK][Bukkit] Bot message: msgId=%s, guild=%s(%s), channel=%s(%s), sender=%s(%s), ts=%d, content=%s",
                    msg.getId(),
                    guild.getName(), guild.getId(),
                    channel.getName(), channel.getId(),
                    sender.getFullName(guild), sender.getId(),
                    ts,
                    content
            ));

            // TODO: 在这里解析机器人消息内容，并根据业务处理

        } catch (Throwable t) {
            Whitelist4Kook.getInstance().getLogger().warning("[KOOK][Bukkit] ChannelMessage listener error: " + t.getMessage());
        }
    }
}
