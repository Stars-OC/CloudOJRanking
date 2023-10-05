package xyz.starsoc.event;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import org.jetbrains.annotations.NotNull;
import xyz.starsoc.CloudOJRanking;

public class Command extends JCompositeCommand {
    public static final Command INSTANCE = new Command();
    private static final CloudOJRanking cloudOJ = CloudOJRanking.INSTANCE;
    private Command() {
        super(CloudOJRanking.INSTANCE, "ranking"); // 使用插件主类对象作为指令拥有者；设置主指令名为 "test"
        // 可选设置如下属性
        setDescription("CloudOJRanking插件的总命令行"); // 设置描述，也会在 /help 中展示
        //setPrefixOptional(true); // 设置指令前缀是可选的，即使用 `test` 也能执行指令而不需要 `/test`
    }
    @SubCommand("reload")
    public void reload(@NotNull CommandSender sender) {
        cloudOJ.reload();
        sender.sendMessage("CloudOJRanking插件重载成功");
    }
}
