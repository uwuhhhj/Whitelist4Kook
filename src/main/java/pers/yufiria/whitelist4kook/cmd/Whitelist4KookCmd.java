package pers.yufiria.whitelist4kook.cmd;

import crypticlib.chat.MsgSender;
import crypticlib.command.CommandHandler;
import crypticlib.command.CommandInfo;
import crypticlib.command.SubcommandHandler;
import crypticlib.command.annotation.Command;
import crypticlib.command.annotation.Subcommand;
import crypticlib.perm.PermInfo;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import pers.yufiria.whitelist4kook.Whitelist4Kook;
import org.jetbrains.annotations.NotNull;
import pers.yufiria.whitelist4kook.WhitelistManager;
import pers.yufiria.whitelist4kook.config.Configs;

import java.util.List;
import java.util.Map;

@Command
public class Whitelist4KookCmd extends CommandHandler {

    public static final Whitelist4KookCmd INSTANCE = new Whitelist4KookCmd();

    Whitelist4KookCmd() {
        super(new CommandInfo("whitelist4kook", new PermInfo("whitelist4kook.command"), new String[]{"wlk"}));
    }

    @Subcommand
    SubcommandHandler reload = new SubcommandHandler("reload", new PermInfo("whitelist4kook.command.reload")) {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            Whitelist4Kook.getInstance().reloadConfig();
            //TODO
            MsgSender.sendMsg(sender, Configs.langCommandReload.value());
            return true;
        }
    };

    @Subcommand
    SubcommandHandler remove = new SubcommandHandler("remove", new PermInfo("whitelist4kook.command.remove")) {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.isEmpty()) {
                MsgSender.sendMsg(sender, Configs.langCommandRemoveNoPlayer.value());;
                return true;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
            WhitelistManager.removeBind(player.getUniqueId());
            MsgSender.sendMsg(sender, Configs.langCommandRemoveSuccess.value(), Map.of("%player%", args.get(0)));
            return true;
        }
    };

}
