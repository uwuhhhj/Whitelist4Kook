package pers.yufiria.whitelist4kook;

import pers.yufiria.whitelist4kook.config.Configs;
import pers.yufiria.whitelist4kook.data.DataManager;
import pers.yufiria.whitelist4kook.data.HikariCPUtil;
import pers.yufiria.whitelist4kook.event.PlayerListener;
import crypticlib.BukkitPlugin;
import org.bukkit.Bukkit;
import pers.yufiria.kookmc.KookMC;
import snw.jkook.command.JKookCommand;
import snw.jkook.entity.Guild;
import snw.jkook.entity.channel.TextChannel;
import snw.jkook.message.TextChannelMessage;

import java.util.UUID;

public final class Whitelist4Kook extends BukkitPlugin {

    private static Whitelist4Kook INSTANCE;

    @Override
    public void enable() {
        INSTANCE = this;
        HikariCPUtil.initHikariCP();
        DataManager.createTable();
        Bukkit.getPluginManager().registerEvents(PlayerListener.INSTANCE, this);
        regWhitelistCmd();
    }

    private void regWhitelistCmd() {
        KookMC.getInstance().regKookCommand(new JKookCommand("whitelist", "/")
                .addAlias("wl")
                .executesUser((user, arguments, message) -> {
                    if (!isChannelAllowBind((TextChannelMessage) message))
                        return;
                    if (WhitelistManager.getBind(user) != null) {
                        String replyMsg = Configs.langBotBindBound.value();
                        String playerName = Bukkit.getOfflinePlayer(UUID.fromString(WhitelistManager.getBind(user))).getName();
                        replyMsg = replyMsg.replace("%player%", playerName);
                        message.reply(replyMsg);
                        return;
                    }
                    if (arguments.length < 1) {
                        message.reply(Configs.langBotBindEnterCode.value());
                        return;
                    }
                    String code = arguments[0].toString();
                    code = code.replace("\\s", "");
                    if (!WhitelistManager.getBindCodeMap().containsKey(code)) {
                        message.reply(Configs.langBotBindNotExistCode.value());
                        return;
                    }
                    UUID uuid = WhitelistManager.getBindCodeMap().get(code);
                    WhitelistManager.addBind(WhitelistManager.getBindCodeMap().get(code), user);
                    String replyMsg = Configs.langBotBindSuccess.value();
                    replyMsg = replyMsg.replace("%player%", WhitelistManager.getPlayerNameCache(uuid));
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
        if (!Configs.bindGuilds.value().contains(guild.getId())) {
            return false;
        }
        return Configs.bindChannels.value().contains(channel.getId());
    }

    @Override
    public void disable() {
        // Plugin shutdown logic
    }


    public static Whitelist4Kook getInstance() {
        return INSTANCE;
    }

}
