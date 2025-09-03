package pers.yufiria.whitelist4kook;

import pers.yufiria.whitelist4kook.config.Configs;
import pers.yufiria.whitelist4kook.data.DataManager;
import pers.yufiria.whitelist4kook.data.HikariCPUtil;
import pers.yufiria.whitelist4kook.event.PlayerListener;
import pers.yufiria.whitelist4kook.event.KookMcMessageListener;
import crypticlib.BukkitPlugin;
import org.bukkit.Bukkit;
import pers.yufiria.kookmc.KookMC;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.Guild;
import snw.jkook.entity.User;
import snw.jkook.entity.channel.Channel;
import snw.jkook.message.ChannelMessage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;
import java.util.Collection;
import java.util.logging.Level;

public final class Whitelist4Kook extends BukkitPlugin {

    private static Whitelist4Kook INSTANCE;
    private volatile boolean whitelistEnabled;

    @Override
    public void enable() {
        INSTANCE = this;
        HikariCPUtil.initHikariCP();
        DataManager.createTable();
        Bukkit.getPluginManager().registerEvents(PlayerListener.INSTANCE, this);
        Bukkit.getPluginManager().registerEvents(KookMcMessageListener.INSTANCE, this);
        // Load whitelist enabled flag from config
        whitelistEnabled = Configs.enabled.value();
        regKookUserBindCmd();
    }

    private void regKookUserBindCmd() {
        KookMC.getInstance().regKookCommand(new JKookCommand("bind", "/")
                .addAlias("bd")
                .executesUser((user, arguments, message) -> {
                    if (!isChannelAllowBind((ChannelMessage) message))
                        return;
                    // 若已绑定，返回绑定信息
                    if (WhitelistManager.getBind(user) != null) {
                        String replyMsg = Configs.langBotBindBound.value();
                        String playerName = Bukkit.getOfflinePlayer(UUID.fromString(WhitelistManager.getBind(user))).getName();
                        replyMsg = replyMsg.replace("%player%", playerName);
                        message.reply(replyMsg);
                        return;
                    }
                    // 未输入绑定码
                    if (arguments.length < 1) {
                        message.reply(Configs.langBotBindEnterCode.value());
                        return;
                    }
                    String code = arguments[0].toString().replace("\\s", "");
                    // 绑定码不存在
                    if (!WhitelistManager.getBindCodeMap().containsKey(code)) {
                        message.reply(Configs.langBotBindNotExistCode.value());
                        return;
                    }
                    // 防重复绑定：UUID 是否已绑定其他 KOOK 账号
                    UUID uuid = WhitelistManager.getBindCodeMap().get(code);
                    if (WhitelistManager.getBind(uuid) != null) {
                        message.reply(Configs.langBotBindUuidAlreadyBound.value());
                        return;
                    }
                    // 执行绑定
                    WhitelistManager.addBind(uuid, user);
                    String replyMsg = Configs.langBotBindSuccess.value();
                    replyMsg = replyMsg.replace("%player%", WhitelistManager.getPlayerNameCache(uuid));
                    message.reply(replyMsg);
                    WhitelistManager.removeBindCodeCache(code);
                    // 绑定成功后尝试授予“玩家”角色
                    try {
                        Guild guild = ((ChannelMessage) message).getChannel().getGuild();
                        int roleId = getRoleIdFromConfig("玩家");
                        if (roleId > 0) {
                            try {
                                user.grantRole(guild, roleId);
                            } catch (Throwable t) {
                                getLogger().log(Level.WARNING,
                                        "Failed to grant role to user. guild={0}, user={1}, roleId={2}",
                                        new Object[]{guild.getId(), user.getId(), roleId});
                                getLogger().log(Level.WARNING, "Grant role exception", t);
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }));

        KookMC.getInstance().regKookCommand(new JKookCommand("你好", "/")
                .executesUser((user, arguments, message) -> {
                    Guild guild = ((ChannelMessage) message).getChannel().getGuild();
                    String topRole = getTopRoleByOrder(user, guild);
                    if (topRole != null) {
                        message.reply("你好" + topRole);
                    } else {
                        message.reply("你好你没有角色");
                    }
                }));

        KookMC.getInstance().regKookCommand(new JKookCommand("bind-kookid-search", "/")
                .executesUser((user, arguments, message) -> {
                    Guild guild = ((ChannelMessage) message).getChannel().getGuild();
                    String topRole = getTopRoleByOrder(user, guild);
                    if (topRole == null) {
                        message.reply("你没有权限进行查询");
                        return;
                    }
                    if (arguments.length < 1) {
                        message.reply("请输入查询的kookid参数");
                        return;
                    }
                    String kookId = arguments[0].toString().trim();
                    if (!kookId.matches("\\d+")) {
                        message.reply("kookid必须为纯数字");
                        return;
                    }
                    String uuidStr = DataManager.getBind(kookId);
                    if (uuidStr == null || uuidStr.isEmpty()) {
                        message.reply("未找到该KOOK ID的绑定记录");
                        return;
                    }
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                        if (playerName == null || playerName.isEmpty()) {
                            message.reply("查询到绑定，但未获取到玩家名");
                        } else {
                            message.reply("绑定的玩家：" + playerName);
                        }
                    } catch (Throwable t) {
                        message.reply("查询到记录，但UUID无效：" + uuidStr);
                    }
                }));

        KookMC.getInstance().regKookCommand(new JKookCommand("bind-player-search", "/")
                .executesUser((user, arguments, message) -> {
                    Guild guild = ((ChannelMessage) message).getChannel().getGuild();
                    String topRole = getTopRoleByOrder(user, guild);
                    if (topRole == null) {
                        message.reply("你没有权限进行查询");
                        return;
                    }
                    if (arguments.length < 1) {
                        message.reply("请输入查询的玩家名字");
                        return;
                    }
                    String playerName = arguments[0].toString().trim();
                    if (playerName.isEmpty()) {
                        message.reply("玩家名不能为空");
                        return;
                    }
                    try {
                        UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                        if (uuid == null) {
                            message.reply("未找到该玩家的UUID");
                            return;
                        }
                        String kookId = DataManager.getBind(uuid);
                        if (kookId == null || kookId.isEmpty()) {
                            message.reply("未找到该玩家的绑定记录");
                        } else {
                            message.reply("绑定的KOOK ID：" + kookId);
                        }
                    } catch (Throwable t) {
                        message.reply("查询失败，请检查玩家名是否正确");
                    }
                }));
    }

    private int getRoleIdFromConfig(String key) {
        try {
            ConfigurationSection section = Configs.roles.value();
            if (section == null) return -1;
            String idStr = section.getString(key);
            if (idStr == null || idStr.isEmpty()) return -1;
            return Integer.parseInt(idStr.trim().replace("'", ""));
        } catch (Throwable ignored) {
            return -1;
        }
    }

    // 根据 config.yml 中 roles 的定义顺序，返回用户在该服务器的最高等级角色名称；如果没有匹配则返回 null
    private String getTopRoleByOrder(User user, Guild guild) {
        try {
            ConfigurationSection section = Configs.roles.value();
            if (section == null) return null;
            Collection<Integer> userRoles = user.getRoles(guild);
            if (userRoles == null || userRoles.isEmpty()) return null;

            for (String roleName : section.getKeys(false)) { // 迭代顺序即为映射顺序
                String idStr = section.getString(roleName);
                if (idStr == null) continue;
                try {
                    int id = Integer.parseInt(idStr.trim().replace("'", ""));
                    if (userRoles.contains(id)) {
                        return roleName;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            return null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private boolean isChannelAllowBind(ChannelMessage message) {
        Channel channel = message.getChannel();
        Guild guild = channel.getGuild();
        var guilds = Configs.bindGuilds.value();
        var channels = Configs.bindChannels.value();
        boolean restrictGuilds = guilds != null && !guilds.isEmpty() && !(guilds.size() == 1 && guilds.contains("0"));
        boolean restrictChannels = channels != null && !channels.isEmpty() && !(channels.size() == 1 && channels.contains("0"));
        if (restrictGuilds && !guilds.contains(guild.getId())) {
            return false;
        }
        if (restrictChannels && !channels.contains(channel.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public void disable() {
        // Plugin shutdown logic
    }

    public static Whitelist4Kook getInstance() {
        return INSTANCE;
    }

    public boolean isWhitelistEnabled() {
        return whitelistEnabled;
    }

    public void setWhitelistEnabled(boolean enabled) {
        this.whitelistEnabled = enabled;
        try {
            getConfig().set("enabled", enabled);
            saveConfig();
        } catch (Throwable ignored) {
        }
    }
}
