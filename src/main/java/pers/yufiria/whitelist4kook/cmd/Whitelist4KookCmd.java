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
import org.bukkit.entity.Player;
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
            MsgSender.sendMsg(sender, Configs.langCommandReload.value());
            return true;
        }
    };

    @Subcommand
    SubcommandHandler remove = new SubcommandHandler("playerbindremove", new PermInfo("whitelist4kook.command.remove")) {
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

    @Subcommand
    SubcommandHandler bind = new SubcommandHandler("bind", new PermInfo("whitelist4kook.command.bind")) {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (!(sender instanceof Player)) {
                MsgSender.sendMsg(sender, "&c该子命令仅限玩家执行");
                return true;
            }

            Player player = (Player) sender;
            if (WhitelistManager.getBind(player.getUniqueId()) != null) {
                MsgSender.sendMsg(player, Configs.langBotBindBound.value(), Map.of("%player%", player.getName()));
                return true;
            }

            String code;
            if (WhitelistManager.getReverseBindCodeMap().containsKey(player.getUniqueId())) {
                code = WhitelistManager.getReverseBindCodeMap().get(player.getUniqueId());
            } else {
                code = java.util.UUID.randomUUID().toString();
                code = code.substring(code.length() - 6);
                WhitelistManager.addBindCodeCache(code, player.getUniqueId(), player.getName());
            }

            String msg = Configs.langBotBindHint.value();
            msg = msg.replace("%code%", code);
            MsgSender.sendMsg(player, msg);
            return true;
        }
    };

    @Subcommand
    SubcommandHandler toggle = new SubcommandHandler("toggle", new PermInfo("whitelist4kook.command.toggle")) {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.size() != 1) {
                MsgSender.sendMsg(sender, "&7用法: /whitelist4kook toggle <on|off>");
                return true;
            }
            String opt = args.get(0).toLowerCase();
            if (opt.equals("on")) {
                Whitelist4Kook.getInstance().setWhitelistEnabled(true);
                MsgSender.sendMsg(sender, "&aWhitelist enabled");
            } else if (opt.equals("off")) {
                Whitelist4Kook.getInstance().setWhitelistEnabled(false);
                MsgSender.sendMsg(sender, "&cWhitelist disabled");
            } else {
                MsgSender.sendMsg(sender, "&7用法: /whitelist4kook toggle <on|off>");
            }
            return true;
        }
    };

    @Subcommand
    SubcommandHandler forcebind = new SubcommandHandler("forcebind", new PermInfo("whitelist4kook.command.forcebind")) {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.size() < 2) {
                MsgSender.sendMsg(sender, "&7用法: /whitelist4kook forcebind <kookId> <playerName>");
                return true;
            }

            String kookId = args.get(0);
            String playerName = args.get(1);

            try {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                java.util.UUID uuid = offlinePlayer.getUniqueId();
                if (uuid == null) {
                    MsgSender.sendMsg(sender, "&c无法获取玩家UUID: " + playerName);
                    return true;
                }

                // 清理可能存在的旧绑定
                String existedKookForUuid = WhitelistManager.getBind(uuid);
                if (existedKookForUuid != null) {
                    MsgSender.sendMsg(sender,"&c此uuid存在绑定，绑定失败，你应该先移除绑定");
                }
                String existedUuidForKook = pers.yufiria.whitelist4kook.data.DataManager.getBind(kookId);
                if (existedUuidForKook != null) {
                    MsgSender.sendMsg(sender,"&c此kookid存在绑定，绑定失败，你应该先移除绑定");
                    return true;
                }

                // 新建绑定
                pers.yufiria.whitelist4kook.data.DataManager.addBind(uuid, kookId);
                MsgSender.sendMsg(sender, "&a已强制绑定 &e" + kookId + " &7<-> &b" + playerName);
            } catch (Throwable t) {
                MsgSender.sendMsg(sender, "&c绑定失败: " + t.getMessage());
            }
            return true;
        }
    };

    @Subcommand
    SubcommandHandler findbind = new SubcommandHandler("findbind", new PermInfo("whitelist4kook.command.findbind")) {
        @Override
        public boolean execute(@NotNull CommandSender sender, @NotNull List<String> args) {
            if (args.size() != 2) {
                MsgSender.sendMsg(sender, "&7用法: /whitelist4kook findbind <kookId|playerName> <纯数字的kookId|游戏idplayerName>");
                return true;
            }

            String mode = args.get(0).trim().toLowerCase();
            String value = args.get(1).trim();
            try {
                switch (mode) {
                    case "kookid": {
                        if (!value.matches("\\d+")) {
                            MsgSender.sendMsg(sender, "&c参数错误: kookId 应为纯数字");
                            return true;
                        }
                        String uuidStr = pers.yufiria.whitelist4kook.data.DataManager.getBind(value);
                        if (uuidStr == null) {
                            MsgSender.sendMsg(sender, "&e未找到绑定: &7kookId=&f" + value);
                            return true;
                        }

                        java.util.UUID uuid = java.util.UUID.fromString(uuidStr);
                        org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(uuid);
                        String name = offlinePlayer != null ? offlinePlayer.getName() : null;
                        if (name == null) name = "<unknown>";
                        MsgSender.sendMsg(sender, "&a绑定信息: &e" + value + " &7<-> &b" + name + " &8(" + uuid + ")");
                        return true;
                    }
                    case "playername": {
                        org.bukkit.OfflinePlayer player = org.bukkit.Bukkit.getOfflinePlayer(value);
                        java.util.UUID uuid = player.getUniqueId();
                        if (uuid == null) {
                            MsgSender.sendMsg(sender, "&c无法获取玩家UUID: " + value);
                            return true;
                        }
                        String kookId = pers.yufiria.whitelist4kook.WhitelistManager.getBind(uuid);
                        if (kookId == null) {
                            MsgSender.sendMsg(sender, "&e未找到绑定: &7player=&f" + value);
                        } else {
                            MsgSender.sendMsg(sender, "&a绑定信息: &b" + value + " &7<-> &e" + kookId + " &8(" + uuid + ")");
                        }
                        return true;
                    }
                    default: {
                        MsgSender.sendMsg(sender, "&7用法: /whitelist4kook findbind <kookId|playerName> <纯数字的kookId|游戏idplayerName>");
                        return true;
                    }
                }
            } catch (Throwable t) {
                MsgSender.sendMsg(sender, "&c查询失败: " + t.getMessage());
            }
            return true;
        }
    };

}
