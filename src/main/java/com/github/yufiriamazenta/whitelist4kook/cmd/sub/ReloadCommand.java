package com.github.yufiriamazenta.whitelist4kook.cmd.sub;

import com.github.yufiriamazenta.lib.command.ISubCommand;
import com.github.yufiriamazenta.lib.util.MsgUtil;
import com.github.yufiriamazenta.whitelist4kook.Whitelist4Kook;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ReloadCommand implements ISubCommand {

    INSTANCE;


    @Override
    public boolean onCommand(CommandSender sender, List<String> args) {
        Whitelist4Kook.getInstance().reloadConfig();
        MsgUtil.sendLang(sender, Whitelist4Kook.getInstance().getConfig(), "lang.command.reload");
        return true;
    }

    @Override
    public String getSubCommandName() {
        return "reload";
    }

    @Override
    public String getPerm() {
        return "whitelist4kook.command.reload";
    }

    @Override
    public void setPerm(String perm) {}

    @Override
    public @NotNull Map<String, ISubCommand> getSubCommands() {
        return new HashMap<>();
    }

}
