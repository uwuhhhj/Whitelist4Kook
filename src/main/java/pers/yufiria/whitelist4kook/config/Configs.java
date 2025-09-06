package pers.yufiria.whitelist4kook.config;

import crypticlib.config.ConfigHandler;
import crypticlib.config.entry.*;

import java.util.List;
import java.util.Map;

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

    // 角色配置：在 config.yml 中以键值对形式定义，例如：
    // roles:
    //   工作人员: '48456844'
    //   玩家: '40572151'
    public static ConfigSectionConfig roles = new ConfigSectionConfig(
            "roles",
            Map.of(
                    "工作人员", "48456844",
                    "玩家", "40572151"
            )
    );

    // 工单内提示信息
    public static StringConfig langTicketNotBindYet = new StringConfig(
            "lang.ticket.not_bind_yet",
            "您尚未绑定游戏ID，无法确认您的 KOOK 对应的游戏身份，请先在主服内完成绑定"
    );
    public static StringConfig langTicketHasBindYet = new StringConfig(
            "lang.ticket.has_bind_yet",
            "当前 KOOK 用户是玩家：%player%"
    );

    public static StringConfig langBotBindEnterCode = new StringConfig("lang.bot.bind.enter_code", "请输入绑定码");
    public static StringConfig langBotBindNotExistCode = new StringConfig("lang.bot.bind.not_exist_code", "不存在的绑定码");
    public static StringConfig langBotBindHint = new StringConfig("lang.bot.bind.hint", "您的绑定码是 %code% ，请加入频道绑定，有效期 5 分钟");
    public static StringConfig langBotBindSuccess = new StringConfig("lang.bot.bind.success", "绑定成功！欢迎您加入服务器，%player%");
    public static StringConfig langBotBindBound = new StringConfig("lang.bot.bind.bound", "您已经绑定了一个账号 %player% ，无法绑定新账号");
    public static StringConfig langBotBindUuidAlreadyBound = new StringConfig("lang.bot.bind.uuid_already_bound", "此玩家已经绑定了其他的 KOOK 账号，您不可以变更绑定！");

    public static StringConfig langBotRemoveBindEnterPlayer = new StringConfig("lang.bot.remove-bind.enter_player", "请输入移除绑定的玩家");
    public static StringConfig langBotRemoveBindNoWhitelist = new StringConfig("lang.bot.remove-bind.no_whitelist", "未查询到此玩家的白名单");
    public static StringConfig langBotRemoveBindSuccess = new StringConfig("lang.bot.remove-bind.success", "已移除 %player% 的白名单");

    public static StringConfig langCommandRemoveSuccess = new StringConfig("lang.command.remove.success", "&a已移除 %player% 绑定的白名单");
    public static StringConfig langCommandRemoveNoPlayer = new StringConfig("lang.command.remove.no_player", "&7请输入移除白名单的玩家名");
    public static StringConfig langCommandReload = new StringConfig("lang.command.reload", "&a已重载配置文件");

    // Common
    public static StringConfig langCommonDbUnavailableTimeout = new StringConfig(
            "lang.common.db_unavailable_timeout",
            "数据库不可用或超时"
    );

    // Bot search/messages
    public static StringConfig langBotSearchNoPermission = new StringConfig(
            "lang.bot.search.no_permission",
            "你没有权限进行查询"
    );
    public static StringConfig langBotSearchEnterKookId = new StringConfig(
            "lang.bot.search.enter_kookid",
            "请输入查询的 kookid 参数"
    );
    public static StringConfig langBotSearchKookIdMustDigits = new StringConfig(
            "lang.bot.search.kookid_must_digits",
            "kookid 必须为纯数字"
    );
    public static StringConfig langBotSearchBoundPlayer = new StringConfig(
            "lang.bot.search.bound_player",
            "绑定的玩家：%player%"
    );
    public static StringConfig langBotSearchFoundButNoName = new StringConfig(
            "lang.bot.search.found_but_no_playername",
            "查询到绑定，但未获取到玩家名"
    );
    public static StringConfig langBotSearchUuidInvalid = new StringConfig(
            "lang.bot.search.uuid_invalid",
            "查询到记录，但 UUID 无效：%uuid%"
    );
    public static StringConfig langBotSearchNoRecordForKookId = new StringConfig(
            "lang.bot.search.no_record_for_kookid",
            "未找到该 KOOK ID 的绑定记录"
    );
    public static StringConfig langBotSearchEnterPlayerName = new StringConfig(
            "lang.bot.search.enter_player_name",
            "请输入查询的玩家名字"
    );
    public static StringConfig langBotSearchPlayerNameEmpty = new StringConfig(
            "lang.bot.search.player_name_empty",
            "玩家名不能为空"
    );
    public static StringConfig langBotSearchUuidNotFound = new StringConfig(
            "lang.bot.search.uuid_not_found",
            "未找到该玩家的 UUID"
    );
    public static StringConfig langBotSearchBoundKookId = new StringConfig(
            "lang.bot.search.bound_kookid",
            "绑定的 KOOK ID：%kookid%"
    );
    public static StringConfig langBotSearchNoRecordForPlayer = new StringConfig(
            "lang.bot.search.no_record_for_player",
            "未找到该玩家的绑定记录"
    );
    public static StringConfig langBotSearchQueryFailCheckName = new StringConfig(
            "lang.bot.search.query_fail_check_name",
            "查询失败，请检查玩家名是否正确"
    );

    // OpenAI / ChatGPT API
    public static StringConfig openaiApiBase = new StringConfig(
            "openai.api_base",
            "https://api.openai.com"
    );
    public static StringConfig openaiToken = new StringConfig(
            "openai.token",
            ""
    );

}
