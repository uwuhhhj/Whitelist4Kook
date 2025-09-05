package pers.yufiria.whitelist4kook.kook;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.kookmc.KookMC;
import pers.yufiria.whitelist4kook.Whitelist4Kook;
import pers.yufiria.whitelist4kook.WhitelistManager;
import pers.yufiria.whitelist4kook.config.Configs;
import pers.yufiria.whitelist4kook.data.DataManager;
import pers.yufiria.whitelist4kook.util.Async;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.message.ChannelMessage;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public final class KookUserBindCommands {
    private KookUserBindCommands() {}

    public static void register() {
        // /bind
        KookMC.getInstance().regKookCommand((new JKookCommand("bind", "/")).addAlias("bd").executesUser((user, arguments, message) -> {
            if (!(message instanceof ChannelMessage) || !isChannelAllowBind((ChannelMessage) message)) return;

            Async.supplyDb(Whitelist4Kook.getInstance(),
                    () -> WhitelistManager.getBind(user),
                    bind -> {
                        if (bind != null) {
                            String replyMsg = (String) Configs.langBotBindBound.value();
                            String playerName = Bukkit.getOfflinePlayer(UUID.fromString(bind)).getName();
                            if (playerName == null) playerName = "";
                            replyMsg = replyMsg.replace("%player%", playerName);
                            message.reply(replyMsg);
                            return;
                        }
                        if (arguments.length < 1) {
                            message.reply((String) Configs.langBotBindEnterCode.value());
                            return;
                        }
                        String code = arguments[0].toString().replace("\\s", "");
                        if (!WhitelistManager.getBindCodeMap().containsKey(code)) {
                            message.reply((String) Configs.langBotBindNotExistCode.value());
                            return;
                        }

                        UUID uuid = (UUID) WhitelistManager.getBindCodeMap().get(code);
                        Async.runDb(Whitelist4Kook.getInstance(),
                                () -> WhitelistManager.addBind(uuid, user),
                                t -> {
                                    Whitelist4Kook.getInstance().getLogger().log(Level.WARNING, "DB unavailable or timeout on bind: {0}", t.getMessage());
                                    message.reply((String) Configs.langCommonDbUnavailableTimeout.value());
                                },
                                () -> {
                                    String replyMsg = (String) Configs.langBotBindSuccess.value();
                                    replyMsg = replyMsg.replace("%player%", WhitelistManager.getPlayerNameCache(uuid));
                                    message.reply(replyMsg);
                                    WhitelistManager.removeBindCodeCache(code);

                                    try {
                                        Guild guild = ((ChannelMessage) message).getChannel().getGuild();
                                        int roleId = getRoleIdFromConfig("玩家");
                                        if (roleId > 0) {
                                            try {
                                                user.grantRole(guild, roleId);
                                            } catch (Throwable t2) {
                                                Whitelist4Kook.getInstance().getLogger().log(Level.WARNING, "Failed to grant role to user. guild={0}, user={1}, roleId={2}", new Object[]{guild.getId(), user.getId(), roleId});
                                                Whitelist4Kook.getInstance().getLogger().log(Level.WARNING, "Grant role exception", t2);
                                            }
                                        }
                                    } catch (Throwable ignore) {
                                    }
                                }
                        );
                    },
                    t -> {
                        Whitelist4Kook.getInstance().getLogger().log(Level.WARNING, "DB unavailable or timeout on bind precheck: {0}", t.getMessage());
                        message.reply((String) Configs.langCommonDbUnavailableTimeout.value());
                    }
            );
        }));

        // /bind-kookid-search
        KookMC.getInstance().regKookCommand((new JKookCommand("bind-kookid-search", "/")).executesUser((user, arguments, message) -> {
            if (!(message instanceof ChannelMessage)) return;
            Guild guild = ((ChannelMessage) message).getChannel().getGuild();
            String topRole = getTopRoleByOrder(user, guild);
            if (topRole == null) {
                message.reply((String) Configs.langBotSearchNoPermission.value());
            } else if (arguments.length < 1) {
                message.reply((String) Configs.langBotSearchEnterKookId.value());
            } else {
                String kookId = arguments[0].toString().trim();
                if (!kookId.matches("\\d+")) {
                    message.reply((String) Configs.langBotSearchKookIdMustDigits.value());
                } else {
                    Async.supplyDb(Whitelist4Kook.getInstance(),
                            () -> DataManager.getBind(kookId),
                            uuidStr -> {
                                if (uuidStr != null && !uuidStr.isEmpty()) {
                                    try {
                                        UUID uuid = UUID.fromString(uuidStr);
                                        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                                        if (playerName != null && !playerName.isEmpty()) {
                                            String msg = (String) Configs.langBotSearchBoundPlayer.value();
                                            message.reply(msg.replace("%player%", playerName));
                                        } else {
                                            message.reply((String) Configs.langBotSearchFoundButNoName.value());
                                        }
                                    } catch (Throwable ex) {
                                        String msg = (String) Configs.langBotSearchUuidInvalid.value();
                                        message.reply(msg.replace("%uuid%", uuidStr));
                                    }
                                } else {
                                    message.reply((String) Configs.langBotSearchNoRecordForKookId.value());
                                }
                            },
                            t -> message.reply((String) Configs.langCommonDbUnavailableTimeout.value())
                    );
                }
            }
        }));

        // /bind-player-search
        KookMC.getInstance().regKookCommand((new JKookCommand("bind-player-search", "/")).executesUser((user, arguments, message) -> {
            if (!(message instanceof ChannelMessage)) return;
            Guild guild = ((ChannelMessage) message).getChannel().getGuild();
            String topRole = getTopRoleByOrder(user, guild);
            if (topRole == null) {
                message.reply((String) Configs.langBotSearchNoPermission.value());
            } else if (arguments.length < 1) {
                message.reply((String) Configs.langBotSearchEnterPlayerName.value());
            } else {
                String playerName = arguments[0].toString().trim();
                if (playerName.isEmpty()) {
                    message.reply((String) Configs.langBotSearchPlayerNameEmpty.value());
                } else {
                    try {
                        UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                        if (uuid == null) {
                            message.reply((String) Configs.langBotSearchUuidNotFound.value());
                            return;
                        }
                        Async.supplyDb(Whitelist4Kook.getInstance(),
                                () -> DataManager.getBind(uuid),
                                kookId -> {
                                    if (kookId != null && !kookId.isEmpty()) {
                                        String msg = (String) Configs.langBotSearchBoundKookId.value();
                                        message.reply(msg.replace("%kookid%", kookId));
                                    } else {
                                        message.reply((String) Configs.langBotSearchNoRecordForPlayer.value());
                                    }
                                },
                                t -> message.reply((String) Configs.langCommonDbUnavailableTimeout.value())
                        );
                    } catch (Throwable ex) {
                        message.reply((String) Configs.langBotSearchQueryFailCheckName.value());
                    }
                }
            }
        }));

        // /你好 (test)
        KookMC.getInstance().regKookCommand((new JKookCommand("你好", "/")).executesUser((user, arguments, message) -> {
            if (!(message instanceof ChannelMessage)) return;
            Guild guild = ((ChannelMessage) message).getChannel().getGuild();
            String topRole = getTopRoleByOrder(user, guild);
            message.reply("你好" + topRole);
        }));
    }

    private static int getRoleIdFromConfig(String key) {
        try {
            ConfigurationSection section = (ConfigurationSection) Configs.roles.value();
            if (section == null) {
                return -1;
            } else {
                String idStr = section.getString(key);
                return idStr != null && !idStr.isEmpty() ? Integer.parseInt(idStr.trim().replace("'", "")) : -1;
            }
        } catch (Throwable var4) {
            return -1;
        }
    }

    private static String getTopRoleByOrder(User user, Guild guild) {
        try {
            ConfigurationSection section = (ConfigurationSection) Configs.roles.value();
            if (section == null) {
                return null;
            } else {
                Collection<Integer> userRoles = user.getRoles(guild);
                if (userRoles != null && !userRoles.isEmpty()) {
                    for (String roleName : section.getKeys(false)) {
                        String idStr = section.getString(roleName);
                        if (idStr != null) {
                            try {
                                int id = Integer.parseInt(idStr.trim().replace("'", ""));
                                if (userRoles.contains(id)) {
                                    return roleName;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                    return null;
                } else {
                    return null;
                }
            }
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean isChannelAllowBind(ChannelMessage message) {
        snw.jkook.entity.channel.Channel channel = message.getChannel();
        Guild guild = channel.getGuild();
        return ((List<?>) Configs.bindGuilds.value()).contains(guild.getId()) && ((List<?>) Configs.bindChannels.value()).contains(channel.getId());
    }
}

