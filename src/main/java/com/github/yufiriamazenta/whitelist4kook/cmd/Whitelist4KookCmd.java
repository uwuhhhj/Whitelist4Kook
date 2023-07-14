package com.github.yufiriamazenta.whitelist4kook.cmd;

import com.github.yufiriamazenta.lib.command.IPluginCommand;
import com.github.yufiriamazenta.lib.command.ISubCommand;
import com.github.yufiriamazenta.whitelist4kook.Whitelist4Kook;
import com.github.yufiriamazenta.whitelist4kook.cmd.sub.ReloadCommand;
import com.github.yufiriamazenta.whitelist4kook.cmd.sub.RemoveBindCmd;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public enum Whitelist4KookCmd implements IPluginCommand {

    INSTANCE;

    private final Map<String, ISubCommand> subCommandMap;

    Whitelist4KookCmd() {
        subCommandMap = new HashMap<>();
        regSubCommand(RemoveBindCmd.INSTANCE);
        regSubCommand(ReloadCommand.INSTANCE);
    }
    @Override
    public Plugin getPlugin() {
        return Whitelist4Kook.getInstance();
    }

    @Override
    public @NotNull Map<String, ISubCommand> getSubCommands() {
        return subCommandMap;
    }
}
