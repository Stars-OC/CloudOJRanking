package xyz.starsoc.event;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.PlainText;
import org.jetbrains.annotations.NotNull;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.Message;
import xyz.starsoc.ranking.data.UpdateRankerMapper;

import java.util.Set;

public class GroupMsg extends SimpleListenerHost {

    private final Config config = Config.INSTANCE;
    private final Message message = Message.INSTANCE;
    private final Set<Long> groupList = config.getEnableGroup();

    private UpdateRankerMapper mapper = UpdateRankerMapper.INSTANCE;

    @EventHandler
    public void onMessage(@NotNull GroupMessageEvent event) {// 可以抛出任何异常, 将在 handleException 处理

        Group group = event.getGroup();
        long groupId = group.getId();
        if (!groupList.contains(groupId)){
            return;
        }

        String plain = event.getMessage().get(PlainText.Key).contentToString();
        if (!(plain.startsWith("!ranking") || plain.startsWith("！ranking"))){
            return;
        }

        String help = message.getHelp();

        String[] command = plain.split(" ");
        if (command.length == 1){
            group.sendMessage(help);
        }

        switch (command[1]){
            case "排行榜":
                if(mapper.getRankingUpNow(group)){
                    group.sendMessage(UpdateRankerMapper.rankingUpMessageNow);
                }else {
                    group.sendMessage("今天暂未有人上榜，请过会再来看看吧...");
                }
                return;
            case "昨日排行榜":
                ForwardMessage ranking = UpdateRankerMapper.rankingUpMessage;
                if (ranking == null){
                    group.sendMessage("昨天暂未有人上榜，请明天再来看看吧...");
                    return;
                }
                group.sendMessage(ranking);
                return;
            case "help":
                group.sendMessage(help);
                return;
            default:
                group.sendMessage(help);
        }
    }
}
