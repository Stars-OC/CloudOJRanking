package xyz.starsoc.ranking.data;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.Data;
import xyz.starsoc.file.Message;
import xyz.starsoc.object.Ranker;
import xyz.starsoc.object.UpdateRanker;

import java.util.ArrayList;
import java.util.Map;

public class UpdateRankerMapper {

    public static final UpdateRankerMapper INSTANCE = new UpdateRankerMapper();

    public static final ArrayList<UpdateRanker> updateRankers = new ArrayList<>();
    private static Logger logger = LoggerFactory.getLogger("RankingThread");


    private static final Config config = Config.INSTANCE;
    private static final Message message = Message.INSTANCE;
    private static final Data data = Data.INSTANCE;
    private static Map<String, Ranker> rankerMap = data.getPersons();


    public void test() {
        System.out.println("ranker ：" + updateRankers.size());
        for(UpdateRanker ranker : updateRankers){
            System.out.println(ranker.getUserId() + " : " + ranker.getRank());
        }
        updateRankers.clear();
    }

    public void sendMessage(){
        if(updateRankers.isEmpty()){
            logger.info("没有人在卷QAQ");
            return;
        }

        Bot bot = Bot.getInstanceOrNull(config.getBot());

        for (String groupId : config.getEnableGroup()){

            Group group = bot.getGroupOrFail(Long.parseLong(groupId));
            ForwardMessageBuilder builder = new ForwardMessageBuilder(group);

            for(UpdateRanker updateRanker : updateRankers){

                String msg = "";
                String userId = updateRanker.getUserId();
                Ranker ranker = rankerMap.get(userId);
                String name = ranker.getName() + "(" + userId + ")";
                int rank = updateRanker.getRank();
                if(rank > 0){
                    Integer newRank = ranker.getRank();
                    msg += message.getRankUp()
                            .replace("%name%",name)
                            .replace("%rankUp%", rank + "")
                            .replace("%oldRank%",(newRank + rank) + "")
                            .replace("%newRank%",newRank + "");
                }

                double score = updateRanker.getScore();
                if(score > 0){
                    msg += message.getScoreUp()
                            .replace("%name%",name)
                            .replace("%scoreUp%",score + "");
                }

                String text = updateRanker.getText();
                if (text != null){
                    msg += text + "\n";
                }

                msg += message.getSuffix();
                builder.add(bot.getId(),"CloudOJ推送",new PlainText(msg));

            }

            group.sendMessage(builder.build());

        }

        updateRankers.clear();
    }
}
