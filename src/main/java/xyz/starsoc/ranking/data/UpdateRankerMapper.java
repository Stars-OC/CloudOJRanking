package xyz.starsoc.ranking.data;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.Data;
import xyz.starsoc.file.Message;
import xyz.starsoc.object.Ranker;
import xyz.starsoc.object.UpdateRanker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateRankerMapper {

    public static final UpdateRankerMapper INSTANCE = new UpdateRankerMapper();

    public static final ArrayList<UpdateRanker> updateRankers = new ArrayList<>();
    public static final HashMap<String,UpdateRanker> rankingUp = new HashMap<>();
    private static final ArrayList<Group> groupList = new ArrayList<>();
    public static ForwardMessage rankingUpMessage = null;
    public static ForwardMessage rankingUpMessageNow = null;
    private static Logger logger = LoggerFactory.getLogger("RankingThread");


    private static final Config config = Config.INSTANCE;
    private static final Message message = Message.INSTANCE;
    private static final Data data = Data.INSTANCE;
    private static Map<String, Ranker> rankerMap = data.getPersons();


    public void init() {
        //初始化
        updateForward();
    }

    private void updateRankingUp(UpdateRanker updateRanker){
        String userId = updateRanker.getUserId();

        //用来添加上榜人员
        if(!rankingUp.containsKey(userId)){
            rankingUp.put(userId,updateRanker);
        }else {
            UpdateRanker oldRanker = rankingUp.get(userId);
            oldRanker.setScore(oldRanker.getScore() + updateRanker.getScore());
            oldRanker.setRank(oldRanker.getRank() + updateRanker.getRank());
            oldRanker.setPassed(oldRanker.getPassed() + updateRanker.getPassed());
            //Text 暂时不需要
        }
    }

    public boolean getRankingUpNow(Group group){

        if(rankingUp.isEmpty()){
            return false;
        }

        ForwardMessageBuilder builder = new ForwardMessageBuilder(group);
        Date dateTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String today = simpleDateFormat.format(dateTime);

        builder.add(config.getBot(),"CloudOJ日榜",new PlainText(message.getPrefixRankingUp().replace("%date%",today)));
        buildRankingUp(builder);
        rankingUpMessageNow = builder.build();

        return true;
    }

    public void sendRankingUpNow(){

        for(Group group : groupList){

            if(getRankingUpNow(group)){
                group.sendMessage(rankingUpMessageNow);
            }

        }
    }

    public void sendRankingUp(){
        if(rankingUp.isEmpty()){
            logger.info("没有人上榜");
            return;
        }
        //TODO js css 渲染图片

        for(Group group : groupList){

            ForwardMessageBuilder builder = new ForwardMessageBuilder(group);

            Date dateTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String yesterday = simpleDateFormat.format(dateTime.getTime() - 86400);
            builder.add(config.getBot(),"CloudOJ日榜",new PlainText(message.getPrefixRankingUp().replace("%date%",yesterday)));
            buildRankingUp(builder);

            ForwardMessage build = builder.build();
            group.sendMessage(build);
            //TODO 后面测试不同群聊这个的存储方式
            rankingUpMessage = build;

        }

        rankingUp.clear();

    }

    public void buildRankingUp(ForwardMessageBuilder builder){
        Date dateTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String today = simpleDateFormat.format(dateTime);

        rankingUpBuilder(builder);

        builder.add(config.getBot(),"CloudOJ日榜",new PlainText(message.getSuffixRankingUp().replace("%date%",today)));
    }

    private void rankingUpBuilder(ForwardMessageBuilder builder){

        int rankUp = 0;

        if(rankingUp.isEmpty()){
            builder.add(config.getBot(),"CloudOJ日榜",new PlainText("暂未有人上榜..."));
            return;
        }

        for(UpdateRanker updateRanker : rankingUp.values()){
            //TODO 更新排序
            ++rankUp;
            String userId = updateRanker.getUserId();
            Ranker ranker = rankerMap.get(userId);
            String name = ranker.getName() + "(" + userId + ")";

            int rank = updateRanker.getRank();
            int newRank = ranker.getRank();
            double score = updateRanker.getScore();
            int passed = updateRanker.getPassed();
            //TODO 这个可能会出现BUG，后面试试将其进行分开
            builder.add(config.getBot(),"CloudOJ日榜",new PlainText(message.getRankingUp()
                    .replace("%name%",name)
                    .replace("%rankUp%", rank + "")
                    .replace("%oldRank%",(newRank + rank) + "")
                    .replace("%newRank%",newRank + "")
                    .replace("%scoreUp%",score + "")
                    .replace("%passedUp%",passed + "")
                    .replace("%rank%",rankUp + "")));

        }
    }

    private void updateForward(){

        if(!groupList.isEmpty()){
            groupList.clear();
        }

        Bot bot = Bot.getInstanceOrNull(config.getBot());
        for (Long groupId : config.getEnableGroup()){

            Group group = bot.getGroupOrFail(groupId);
            //添加该进行转发的群聊
            groupList.add(group);

        }
    }

    public void sendUpdateMessage(){
        //TODO 用js css 渲染图片发出
        if(updateRankers.isEmpty()){
            logger.info("没有人在卷QAQ");
            return;
        }

        for(Group group : groupList){

            ForwardMessageBuilder builder = new ForwardMessageBuilder(group);

            for(UpdateRanker updateRanker : updateRankers){

                //积累上榜人员
                updateRankingUp(updateRanker);

                //正常发送符合需要的人
                String msg = "";
                String userId = updateRanker.getUserId();
                Ranker ranker = rankerMap.get(userId);
                String name = ranker.getName() + "(" + userId + ")";
                boolean isMonitored = ranker.getRank() <= config.getMonitorLimit();

                int rank = updateRanker.getRank();
                if(rank > config.getRankLimit() || isMonitored){
                    int newRank = ranker.getRank();
                    msg += message.getRankUp()
                            .replace("%name%",name)
                            .replace("%rankUp%", rank + "")
                            .replace("%oldRank%",(newRank + rank) + "")
                            .replace("%newRank%",newRank + "");
                }

                double score = updateRanker.getScore();
                if(score > config.getScoreLimit() || isMonitored){
                    msg += message.getScoreUp()
                            .replace("%name%",name)
                            .replace("%scoreUp%",score + "");
                }

                int passed = updateRanker.getPassed();
                if(passed > config.getPassedLimit() || isMonitored){
                    msg += message.getPassedUp()
                            .replace("%name%",name)
                            .replace("%passedUp%",passed + "")
                            .replace("%passed%",ranker.getPassed() + "");
                }

                String text = updateRanker.getText();
                if (text != null){
                    msg += text.replace("%name%",name) + "\n";
                }

                msg += message.getSuffix();
                builder.add(config.getBot(),"CloudOJ推送", new PlainText(msg));
            }
            group.sendMessage(builder.build());
        }

        updateRankers.clear();

    }
}
