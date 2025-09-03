package pers.yufiria.whitelist4kook.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.entry.*;

import java.util.List;

@ConfigHandler(path = "config.yml")
public class Configs {

    public static BooleanConfig enabled = new BooleanConfig("enabled", true);

    public static StringConfig hikariCPDriver = new StringConfig("hikariCP.driver", "com.mysql.cj.jdbc.Driver");
    public static IntConfig hikariCPConnectionTimeout = new IntConfig("hikariCP.connectionTimeout", 30000);
    public static IntConfig hikariCPMaxLifeTime = new IntConfig("hikariCP.maxLifeTime", 30000);
    public static IntConfig hikariCPMinimumIdle = new IntConfig("hikariCP.minimumIdle", 30000);
    public static IntConfig hikariCPMaxPoolSize = new IntConfig("hikariCP.maximumPoolSize", 50);
    public static BooleanConfig hikariCPAutoCommit = new BooleanConfig("hikariCP.autoCommit", true);

    public static StringConfig mysqlAddress = new StringConfig("mysql.address", "localhost");
    public static IntConfig mysqlPort = new IntConfig("mysql.port", 3306);
    public static StringConfig mysqlDatabase = new StringConfig("mysql.database", "whitelist");
    public static StringConfig mysqlUser = new StringConfig("mysql.user", "root");
    public static StringConfig mysqlPassword = new StringConfig("mysql.password", "123456");
    public static StringConfig mysqlArgs = new StringConfig("mysql.args", "?useUnicode=true&allowPublicKeyRetrieval=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai");

    public static IntConfig codeTimeoutSecond = new IntConfig("codeTimeoutSecond", 300);
    public static StringListConfig bindGuilds = new StringListConfig("bind_guilds", List.of());
    public static StringListConfig bindChannels = new StringListConfig("bind_channels", List.of());

    public static StringConfig langBotBindEnterCode = new StringConfig("lang.bot.bind.enter_code", "请输入绑定码");
    public static StringConfig langBotBindNotExistCode = new StringConfig("lang.bot.bind.not_exist_code", "不存在的绑定码");
    public static StringConfig langBotBindHint = new StringConfig("lang.bot.bind.hint", "您的绑定码是%code%，请加入频道绑定，有效期5分钟");
    public static StringConfig langBotBindSuccess = new StringConfig("lang.bot.bind.success", "绑定成功！欢迎您加入服务器，%player%");
    public static StringConfig langBotBindBound = new StringConfig("lang.bot.bind.bound", "您已经绑定了一个账号%player%，无法绑定新账号");
    public static StringConfig langBotRemoveBindEnterPlayer = new StringConfig("lang.bot.remove-bind.enter_player", "请输入移除绑定的玩家");
    public static StringConfig langBotRemoveBindNoWhitelist = new StringConfig("lang.bot.remove-bind.no_whitelist", "未查询到此玩家的白名单");
    public static StringConfig langBotRemoveBindSuccess = new StringConfig("lang.bot.remove-bind.success", "已移除%player%的白名单");
    public static StringConfig langCommandRemoveSuccess = new StringConfig("lang.command.remove.success", "&a已移除%player%绑定的白名单");
    public static StringConfig langCommandRemoveNoPlayer = new StringConfig("lang.command.remove.no_player", "&7请输入移除白名单的玩家名");
    public static StringConfig langCommandReload = new StringConfig("lang.command.reload", "&a已重载配置文件");

}
