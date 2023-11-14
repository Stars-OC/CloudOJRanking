package xyz.starsoc.event;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import org.jetbrains.annotations.NotNull;
import xyz.starsoc.CloudOJRanking;
import xyz.starsoc.file.ContestsDataFile;
import xyz.starsoc.object.ContestData;

import java.util.HashMap;
import java.util.Map;

public class Command extends JCompositeCommand {

    public static final Command INSTANCE = new Command();
    private static final CloudOJRanking cloudOJ = CloudOJRanking.INSTANCE;

    private static final ContestsDataFile contestData = ContestsDataFile.INSTANCE;
    private static final Map<Integer, ContestData> contests = contestData.getContests();
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

    @SubCommand("help")
    public void help(@NotNull CommandSender sender) {
        sender.sendMessage("CloudOJRanking插件的总命令行");
        sender.sendMessage("reload - 重载插件");
        sender.sendMessage("help - 显示帮助信息");
    }

    @SubCommand("invite")
    public void invite(@NotNull CommandSender sender, String contestId,String inviteKey) {

        if (!contests.containsKey(Integer.parseInt(contestId))){
            sender.sendMessage("该竞赛不存在");
        }
        ContestData contest = contests.get(Integer.parseInt(contestId));
        if (!contest.getInviteKey().equals(inviteKey)){
            contest.setInviteKey(inviteKey);
            sender.sendMessage("竞赛 " + contest.getContestName() + " 的邀请码已更新为 [" + inviteKey + "]");
        }else {
            sender.sendMessage("邀请码已存在，添加失败");
        }
    }
}
