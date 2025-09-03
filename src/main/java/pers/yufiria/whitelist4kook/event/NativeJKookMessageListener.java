package pers.yufiria.whitelist4kook.event;

import pers.yufiria.whitelist4kook.Whitelist4Kook;
import pers.yufiria.whitelist4kook.config.Configs;
import snw.jkook.JKook;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.NonCategoryChannel;
import snw.jkook.event.EventHandler;
import snw.jkook.event.Listener;
import snw.jkook.event.channel.ChannelMessageEvent;
import snw.jkook.message.ChannelMessage;
import snw.jkook.message.component.BaseComponent;

public enum NativeJKookMessageListener implements Listener {
    INSTANCE;

    @EventHandler
    public void onChannelMessage(ChannelMessageEvent event) {
        try {
            ChannelMessage msg = event.getMessage();
            User sender = msg.getSender();
            NonCategoryChannel channel = msg.getChannel();
            Guild guild = channel.getGuild();

            // 仅处理当前 Bot 自己发送的消息
            User self = null;
            try {
                self = JKook.getCore() != null ? JKook.getCore().getUser() : null;
            } catch (Throwable ignored) { }

            boolean fromSelf = self != null && sender != null && sender.getId().equals(self.getId());
            boolean fromAnyBot = sender != null && sender.isBot();
            if (!(fromSelf || fromAnyBot)) {
                return; // 不是机器人消息则忽略
            }

            // 可选：限制到配置的允许频道/服务器
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

            BaseComponent component = msg.getComponent();
            String content = component == null ? "" : component.toString();
            long ts = msg.getTimeStamp();

            Whitelist4Kook.getInstance().getLogger().info(String.format(
                    "[KOOK][JKook] Bot message: msgId=%s, guild=%s(%s), channel=%s(%s), sender=%s(%s), ts=%d, content=%s",
                    msg.getId(),
                    guild.getName(), guild.getId(),
                    channel.getName(), channel.getId(),
                    sender.getFullName(guild), sender.getId(),
                    ts,
                    content
            ));

            // TODO: 在此处根据你的业务解析机器人消息内容

        } catch (Throwable t) {
            Whitelist4Kook.getInstance().getLogger().warning("[KOOK][JKook] ChannelMessage listener error: " + t.getMessage());
        }
    }
}
