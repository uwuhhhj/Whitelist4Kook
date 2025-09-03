package pers.yufiria.whitelist4kook;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import pers.yufiria.kookmc.KookMC;
import pers.yufiria.whitelist4kook.config.Configs;
import pers.yufiria.whitelist4kook.data.DataManager;
import pers.yufiria.whitelist4kook.data.HikariCPUtil;
import pers.yufiria.whitelist4kook.event.PlayerListener;
import crypticlib.BukkitPlugin;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.Channel;
import snw.jkook.message.ChannelMessage;

public final class Whitelist4Kook extends BukkitPlugin {
    private static Whitelist4Kook INSTANCE;
    private volatile boolean whitelistEnabled;

    public void enable() {
        INSTANCE = this;
        HikariCPUtil.initHikariCP();
        DataManager.createTable();
        Bukkit.getPluginManager().registerEvents(PlayerListener.INSTANCE, this);
        this.whitelistEnabled = (Boolean)Configs.enabled.value();
        try{
            this.regKookUserBindCmd();
        }catch (Throwable t){
            this.getLogger().log(Level.WARNING, "KOOK内注册指令失败", t);
        }
    }

    private void regKookUserBindCmd() {
        KookMC.getInstance().regKookCommand((new JKookCommand("bind", "/")).addAlias("bd").executesUser((user, arguments, message) -> {
            if (this.isChannelAllowBind((ChannelMessage)message)) {
                if (WhitelistManager.getBind(user) != null) {
                    String replyMsg = (String)Configs.langBotBindBound.value();
                    String playerName = Bukkit.getOfflinePlayer(UUID.fromString(WhitelistManager.getBind(user))).getName();
                    replyMsg = replyMsg.replace("%player%", playerName);
                    message.reply(replyMsg);
                } else if (arguments.length < 1) {
                    message.reply((String)Configs.langBotBindEnterCode.value());
                } else {
                    String code = arguments[0].toString();
                    code = code.replace("\\s", "");
                    if (!WhitelistManager.getBindCodeMap().containsKey(code)) {
                        message.reply((String)Configs.langBotBindNotExistCode.value());
                    } else {
                        UUID uuid = (UUID)WhitelistManager.getBindCodeMap().get(code);
                        WhitelistManager.addBind((UUID)WhitelistManager.getBindCodeMap().get(code), user);
                        String replyMsg = (String)Configs.langBotBindSuccess.value();
                        replyMsg = replyMsg.replace("%player%", WhitelistManager.getPlayerNameCache(uuid));
                        message.reply(replyMsg);
                        WhitelistManager.removeBindCodeCache(code);

                        try {
                            Guild guild = ((ChannelMessage)message).getChannel().getGuild();
                            int roleId = this.getRoleIdFromConfig("玩家");
                            if (roleId > 0) {
                                try {
                                    user.grantRole(guild, roleId);
                                } catch (Throwable t) {
                                    this.getLogger().log(Level.WARNING, "Failed to grant role to user. guild={0}, user={1}, roleId={2}", new Object[]{guild.getId(), user.getId(), roleId});
                                    this.getLogger().log(Level.WARNING, "Grant role exception", t);
                                }
                            }
                        } catch (Throwable var11) {
                        }

                    }
                }
            }
        }));
        KookMC.getInstance().regKookCommand((new JKookCommand("你好", "/")).executesUser((user, arguments, message) -> {
            Guild guild = ((ChannelMessage)message).getChannel().getGuild();
            String topRole = this.getTopRoleByOrder(user, guild);
            if (topRole != null) {
                message.reply("你好" + topRole);
            } else {
                message.reply("你好你没有角色");
            }

        }));
        KookMC.getInstance().regKookCommand((new JKookCommand("bind-kookid-search", "/")).executesUser((user, arguments, message) -> {
            Guild guild = ((ChannelMessage)message).getChannel().getGuild();
            String topRole = this.getTopRoleByOrder(user, guild);
            if (topRole == null) {
                message.reply("你没有权限进行查询");
            } else if (arguments.length < 1) {
                message.reply("请输入查询的kookid参数");
            } else {
                String kookId = arguments[0].toString().trim();
                if (!kookId.matches("\\d+")) {
                    message.reply("kookid必须为纯数字");
                } else {
                    String uuidStr = DataManager.getBind(kookId);
                    if (uuidStr != null && !uuidStr.isEmpty()) {
                        try {
                            UUID uuid = UUID.fromString(uuidStr);
                            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                            if (playerName != null && !playerName.isEmpty()) {
                                message.reply("绑定的玩家：" + playerName);
                            } else {
                                message.reply("查询到绑定，但未获取到玩家名");
                            }
                        } catch (Throwable var10) {
                            message.reply("查询到记录，但UUID无效：" + uuidStr);
                        }

                    } else {
                        message.reply("未找到该KOOK ID的绑定记录");
                    }
                }
            }
        }));
        KookMC.getInstance().regKookCommand((new JKookCommand("bind-player-search", "/")).executesUser((user, arguments, message) -> {
            Guild guild = ((ChannelMessage)message).getChannel().getGuild();
            String topRole = this.getTopRoleByOrder(user, guild);
            if (topRole == null) {
                message.reply("你没有权限进行查询");
            } else if (arguments.length < 1) {
                message.reply("请输入查询的玩家名字");
            } else {
                String playerName = arguments[0].toString().trim();
                if (playerName.isEmpty()) {
                    message.reply("玩家名不能为空");
                } else {
                    try {
                        UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                        if (uuid == null) {
                            message.reply("未找到该玩家的UUID");
                            return;
                        }

                        String kookId = DataManager.getBind(uuid);
                        if (kookId != null && !kookId.isEmpty()) {
                            message.reply("绑定的KOOK ID：" + kookId);
                        } else {
                            message.reply("未找到该玩家的绑定记录");
                        }
                    } catch (Throwable var9) {
                        message.reply("查询失败，请检查玩家名是否正确");
                    }

                }
            }
        }));
    }

    private int getRoleIdFromConfig(String key) {
        try {
            ConfigurationSection section = (ConfigurationSection)Configs.roles.value();
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

    private String getTopRoleByOrder(User user, Guild guild) {
        try {
            ConfigurationSection section = (ConfigurationSection)Configs.roles.value();
            if (section == null) {
                return null;
            } else {
                Collection<Integer> userRoles = user.getRoles(guild);
                if (userRoles != null && !userRoles.isEmpty()) {
                    for(String roleName : section.getKeys(false)) {
                        String idStr = section.getString(roleName);
                        if (idStr != null) {
                            try {
                                int id = Integer.parseInt(idStr.trim().replace("'", ""));
                                if (userRoles.contains(id)) {
                                    return roleName;
                                }
                            } catch (NumberFormatException var9) {
                            }
                        }
                    }

                    return null;
                } else {
                    return null;
                }
            }
        } catch (Throwable var10) {
            return null;
        }
    }

    private boolean isChannelAllowBind(ChannelMessage message) {
        Channel channel = message.getChannel();
        Guild guild = channel.getGuild();
        return !((List)Configs.bindGuilds.value()).contains(guild.getId()) ? false : ((List)Configs.bindChannels.value()).contains(channel.getId());
    }

    public void disable() {
    }

    public static Whitelist4Kook getInstance() {
        return INSTANCE;
    }

    public boolean isWhitelistEnabled() {
        return this.whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean enabled) {
        this.whitelistEnabled = enabled;

        try {
            this.getConfig().set("enabled", enabled);
            this.saveConfig();
        } catch (Throwable var3) {
        }

    }
}
