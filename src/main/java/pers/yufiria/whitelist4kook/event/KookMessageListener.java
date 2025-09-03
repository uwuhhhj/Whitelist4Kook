package pers.yufiria.whitelist4kook.event;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import pers.yufiria.whitelist4kook.Whitelist4Kook;
import pers.yufiria.kookmc.event.kook.channel.KookChannelMessageEvent;
import pers.yufiria.kookmc.event.kook.pm.KookPrivateMessageReceivedEvent;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.Channel;
import snw.jkook.message.ChannelMessage;
import snw.jkook.message.PrivateMessage;
import snw.jkook.message.component.BaseComponent;
import snw.jkook.message.component.card.CardComponent;
import snw.jkook.message.component.card.MultipleCardComponent;
import snw.jkook.message.component.card.CardScopeElement;
import snw.jkook.message.component.card.module.BaseModule;
import snw.jkook.message.component.card.module.SectionModule;
import snw.jkook.message.component.card.module.HeaderModule;
import snw.jkook.message.component.card.element.MarkdownElement;
import snw.jkook.message.component.card.element.PlainTextElement;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum KookMessageListener implements Listener {

    INSTANCE;

    private static final Pattern MENTION_PATTERN = Pattern.compile("\\(met\\)(\\d+)\\(met\\)你的工单");

    @EventHandler
    public void onChannelMessage(KookChannelMessageEvent event) {
        try {
            ChannelMessage msg = event.getMessage();
            User sender = msg.getSender();

            // Only handle messages sent by bots; ignore others
            if (sender == null || !sender.isBot()) {
                return;
            }

            Channel channel = msg.getChannel();
            String content = componentToString(msg.getComponent());

            // If channel name matches pattern like #xxxxx-工单, try to extract @mention KOOK ID
            String chName = channel.getName();
            if (chName != null && chName.startsWith("#") && chName.endsWith("工单")) {
                String mentionedId = extractMentionedKookId(msg.getComponent());
                if (mentionedId == null || mentionedId.isEmpty()) {
                    // fallback: try parse from toString
                    mentionedId = extractMentionedKookIdFromString(content);
                }
                if (mentionedId != null && !mentionedId.isEmpty()) {
                    Whitelist4Kook.getInstance().getLogger().info(
                            String.format("[KOOK][Ticket] channel=%s mentioned_kookid=%s 又双叒叕新公单了", chName, mentionedId)
                    );

                    // Lookup bind by KOOK ID and reply accordingly
                    try {
                        String uuidStr = pers.yufiria.whitelist4kook.data.DataManager.getBind(mentionedId);
                        if (uuidStr == null || uuidStr.isEmpty()) {
                            msg.reply("此用户未绑定游戏id可以前往测试服进行绑定");
                        } else {
                            String playerName = null;
                            try {
                                java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
                                playerName = org.bukkit.Bukkit.getOfflinePlayer(uuid).getName();
                            } catch (Throwable ignored) {
                            }
                            if (playerName == null || playerName.isEmpty()) {
                                msg.reply("当前KOOK用户绑定了玩家，但未获取到玩家名（UUID=" + uuidStr + ")");
                            } else {
                                msg.reply("当前KOOK用户是玩家：" + playerName);
                            }
                        }
                    } catch (Throwable ex) {
                        Bukkit.getLogger().warning("[Whitelist4Kook] Failed to query bind or reply: " + ex.getMessage());
                    }
                }
            }
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[Whitelist4Kook] Failed to log channel message: " + t.getMessage());
        }
    }


    private static String extractMentionedKookId(BaseComponent component) {
        if (component == null) return null;

        try {
            if (component instanceof MultipleCardComponent mcc) {
                for (CardComponent cc : mcc.getComponents()) {
                    String id = extractMentionedKookId(cc);
                    if (id != null && !id.isEmpty()) return id;
                }
            } else if (component instanceof CardComponent cc) {
                List<BaseModule> modules = cc.getModules();
                if (modules != null) {
                    for (BaseModule m : modules) {
                        // Header
                        if (m instanceof HeaderModule header) {
                            String id = extractMentionedKookIdFromCardElement(header.getElement());
                            if (id != null && !id.isEmpty()) return id;
                        }
                        // Section
                        if (m instanceof SectionModule sec) {
                            String id = extractMentionedKookIdFromCardElement(sec.getText());
                            if (id != null && !id.isEmpty()) return id;
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        // Fallback to string content
        return extractMentionedKookIdFromString(component.toString());
    }

    private static String extractMentionedKookIdFromCardElement(Object element) {
        if (element == null) return null;
        try {
            if (element instanceof MarkdownElement me) {
                return extractMentionedKookIdFromString(me.getContent());
            }
            if (element instanceof PlainTextElement pe) {
                return extractMentionedKookIdFromString(pe.getContent());
            }
            if (element instanceof CardScopeElement) {
                // Unknown CardScopeElement subtype, try toString
                return extractMentionedKookIdFromString(element.toString());
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static String extractMentionedKookIdFromString(String text) {
        if (text == null) return null;
        Matcher m = MENTION_PATTERN.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private static String componentToString(BaseComponent component) {
        try {
            return component == null ? "" : component.toString();
        } catch (Throwable ignored) {
            return "<unreadable message>";
        }
    }

    private static String safe(SupplierWithFallback<String> primary, String fallback) {
        try {
            String v = primary.get();
            return v == null ? fallback : v;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    @FunctionalInterface
    private interface SupplierWithFallback<T> {
        T get();
    }
}
