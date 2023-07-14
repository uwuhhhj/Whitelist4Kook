package com.github.yufiriamazenta.whitelist4kook;

import com.github.yufiriamazenta.kookmc.KookMC;
import com.github.yufiriamazenta.lib.util.MsgUtil;
import com.github.yufiriamazenta.whitelist4kook.cmd.Whitelist4KookCmd;
import com.github.yufiriamazenta.whitelist4kook.data.DataManager;
import com.github.yufiriamazenta.whitelist4kook.data.HikariCPUtil;
import com.github.yufiriamazenta.whitelist4kook.event.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import snw.jkook.Permission;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.Guild;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.TextChannelMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public final class Whitelist4Kook extends JavaPlugin {

    private static Whitelist4Kook INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        HikariCPUtil.initHikariCP();
        DataManager.createTable();
        Bukkit.getPluginManager().registerEvents(PlayerListener.INSTANCE, this);
        regWhitelistCmd();
        Bukkit.getPluginCommand("whitelist4kook").setExecutor(Whitelist4KookCmd.INSTANCE);
        Bukkit.getPluginCommand("whitelist4kook").setTabCompleter(Whitelist4KookCmd.INSTANCE);
    }

    private void regWhitelistCmd() {
        KookMC.getInstance().regKookCommand(new JKookCommand("whitelist", "/")
                .addAlias("wl")
                .executesUser((user, arguments, message) -> {
                    if (!isChannelAllowBind((TextChannelMessage) message))
                        return;
                    if (WhitelistManager.getBind(user) != null) {
                        String replyMsg = getConfig().getString("lang.bot.bind.bound", "lang.bot.bind.bound");
                        String playerName = Bukkit.getOfflinePlayer(UUID.fromString(WhitelistManager.getBind(user))).getName();
                        replyMsg = replyMsg.replace("%player%", playerName);
                        message.reply(replyMsg);
                        return;
                    }
                    if (arguments.length < 1) {
                        message.reply(getConfig().getString("lang.bot.bind.enter_code"));
                        return;
                    }
                    String code = arguments[0].toString();
                    code = code.replace("\\s", "");
                    if (!WhitelistManager.getBindCodeMap().containsKey(code)) {
                        message.reply(getConfig().getString("lang.bot.bind.not_exist_code"));
                        return;
                    }
                    UUID uuid = WhitelistManager.getBindCodeMap().get(code);
                    WhitelistManager.addBind(WhitelistManager.getBindCodeMap().get(code), user);
                    String replyMsg = getConfig().getString("lang.bot.bind.success");
                    replyMsg = replyMsg.replace("%player%", Bukkit.getOfflinePlayer(uuid).getName());
                    message.reply(replyMsg);
                    WhitelistManager.removeBindCodeCache(code);
                }));
//        KookMC.getInstance().regKookCommand(new JKookCommand("remove-bind")
//                .addAlias("rmb")
//                .executesUser(((user, args, message) -> {
//                    if (!isChannelAllowBind((TextChannelMessage) message))
//                        return;
//                    //TODO 未做权限校验
//                    Collection<Integer> roleId = user.getRoles(((TextChannelMessage) message).getChannel().getGuild());
//                    if (args.length < 1) {
//                        message.reply(getConfig().getString("lang.bot.remove-bind.enter_player", "lang.bot.remove-bind.enter_player"));
//                        return;
//                    }
//
//                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0].toString());
//                    if (WhitelistManager.getBind(player.getUniqueId()) == null) {
//                        message.reply(getConfig().getString("lang.bot.remove-bind.no_whitelist", "lang.bot.remove-bind.no_whitelist"));
//                        return;
//                    }
//                    WhitelistManager.removeBind(player.getUniqueId());
//                    message.reply(getConfig().getString("lang.bot.remove-bind.success", "lang.bot.remove-bind.success")
//                            .replace("%player%", args[0].toString()));
//                })));
    }

    private boolean isChannelAllowBind(TextChannelMessage message) {
        TextChannel channel = message.getChannel();
        Guild guild = channel.getGuild();
        if (!Whitelist4Kook.INSTANCE.getConfig().getStringList("bind_guilds").contains(guild.getId())) {
            return false;
        }
        return Whitelist4Kook.INSTANCE.getConfig().getStringList("bind_channels").contains(channel.getId());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    public static Whitelist4Kook getInstance() {
        return INSTANCE;
    }

}
