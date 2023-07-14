package com.github.yufiriamazenta.whitelist4kook.cmd.sub;

import com.github.yufiriamazenta.lib.command.ISubCommand;
import com.github.yufiriamazenta.lib.util.MsgUtil;
import com.github.yufiriamazenta.whitelist4kook.Whitelist4Kook;
import com.github.yufiriamazenta.whitelist4kook.WhitelistManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum RemoveBindCmd implements ISubCommand {

    INSTANCE;
    private final Map<String, ISubCommand> subCommandMap;

    RemoveBindCmd() {
        subCommandMap = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        if (args.size() < 1) {
            MsgUtil.sendLang(sender, Whitelist4Kook.getInstance().getConfig(), "lang.command.bind.remove.no_player");
            return true;
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(args.get(0));
        WhitelistManager.removeBind(player.getUniqueId());
        MsgUtil.sendLang(sender, Whitelist4Kook.getInstance().getConfig(), "lang.command.bind.remove.success", Map.of("%player%", player.getName()));
        return true;
    }



    @Override
    public String getSubCommandName() {
        return "remove";
    }

    @Override
    public String getPerm() {
        return "whitelist4kook.command.removebind";
    }

    @Override
    public void setPerm(String perm) {}

    @Override
    public @NotNull Map<String, ISubCommand> getSubCommands() {
        return subCommandMap;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return null;
    }
}
