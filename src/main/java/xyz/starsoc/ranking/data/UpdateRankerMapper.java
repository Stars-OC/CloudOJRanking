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
import java.util.*;

/**
 * @author Clusters_stars
 * 用来加载竞赛信息
 */
public class UpdateRankerMapper {

    public static final UpdateRankerMapper INSTANCE = new UpdateRankerMapper();

    public static final ArrayList<UpdateRanker> updateRankers = new ArrayList<>();
    public static final HashMap<String,UpdateRanker> rankingUp = new HashMap<>();
    public static final ArrayList<Group> groupList = new ArrayList<>();
    public static ForwardMessage rankingUpMessage = null;
    public static ForwardMessage rankingUpMessageNow = null;
    private static final Logger logger = LoggerFactory.getLogger("RankingThread");


    private static final Config config = Config.INSTANCE;
    private static final Message message = Message.INSTANCE;
    private static final Data data = Data.INSTANCE;
    private static Map<String, Ranker> rankerMap = data.getPersons();

    //TODO 将功能进行拆分然后命名好

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

    public void sendRankingUpNow(){

        for(Group group : groupList){

            if(getRankingUpNow(group)){
                group.sendMessage(rankingUpMessageNow);
            }

        }
    }

    public boolean getRankingUpNow(Group group){

        if(rankingUp.isEmpty()){
            return false;
        }

        ForwardMessageBuilder builder = new ForwardMessageBuilder(group);

        buildRankingUp(0,builder);

        rankingUpMessageNow = builder.build();

        return true;
    }

    public void sendRankingUp(){
        if(rankingUp.isEmpty()){
            logger.info("没有人上榜");
            return;
        }
        //TODO js css 渲染图片

        for(Group group : groupList){

            ForwardMessageBuilder builder = new ForwardMessageBuilder(group);

            long oneDaySecond = 86400*1000;

            buildRankingUp(oneDaySecond,builder);

            ForwardMessage build = builder.build();
            group.sendMessage(build);
            rankingUpMessage = build;

        }

        rankingUp.clear();

    }

    public void buildRankingUp(long timeChange,ForwardMessageBuilder builder){

        Date dateTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String today = simpleDateFormat.format(dateTime);
        String time = simpleDateFormat.format(dateTime.getTime() - timeChange);
        builder.add(config.getBot(),"CloudOJ日榜",new PlainText(message.getPrefixRankingUp().replace("%date%",time)));

        rankingUpBuilder(builder);

        //这个可以做实时更新的提示
        builder.add(config.getBot(),"CloudOJ日榜",new PlainText(message.getSuffixRankingUp().replace("%date%",today)));
    }

    private void rankingUpBuilder(ForwardMessageBuilder builder){

        if(rankingUp.isEmpty()){
            builder.add(config.getBot(),"CloudOJ日榜",new PlainText("暂未有人上榜..."));
            return;
        }


        double rankPoint = config.getRankPoint();
        double passedUpPoint = config.getPassedUpPoint();
        double scoreUpPoint = config.getScoreUpPoint();
        double rankUpPoint = config.getRankUpPoint();

        //进行排序
        Collection<UpdateRanker> rankers = rankingUp.values();
        UpdateRanker[] values = rankers.toArray(new UpdateRanker[0]);
        Arrays.sort(values, (o1, o2) -> {
            //排序规则 score*0.8 + passed*5 + rankUp*0.5 - rank
            int rankerO1 = rankerMap.get(o1.getUserId()).getRank();
            int rankerO2 = rankerMap.get(o2.getUserId()).getRank();
            return -((int) ((o1.getScore() - o2.getScore())*scoreUpPoint + (o1.getPassed() - o2.getPassed())*passedUpPoint + (o1.getRank() - o2.getRank())*rankUpPoint - (rankerO1 - rankerO2)*rankPoint));
        });

        for(int i = 0;i < values.length;i++){

            UpdateRanker updateRanker = values[i];
            String userId = updateRanker.getUserId();
            Ranker ranker = rankerMap.get(userId);
            String name = ranker.getNickname() + "(" + userId + ")";

            int rank = updateRanker.getRank();
            int newRank = ranker.getRank();
            double score = updateRanker.getScore();
            int passed = updateRanker.getPassed();
            int NO = i+1;

//            int point = (int)(score * scoreUpPoint + r);

            builder.add(config.getBot(),"CloudOJ日榜",new PlainText(message.getRankingUp()
                    .replace("%name%",name)
                    .replace("%rankUp%", rank + "")
                    .replace("%oldRank%",(newRank + rank) + "")
                    .replace("%newRank%",newRank + "")
                    .replace("%scoreUp%",score + "")
                    .replace("%passedUp%",passed + "")
                    .replace("%rank%",NO + "")));

        }

        builder.add(config.getBot(),"CloudOJ日榜",new PlainText(("排序规则如下：" +
//                "\nscore*%scoreUpPoint% + passed*%passedUpPoint% + rankUp*%rankUpPoint% - rank*%rankPoint%" +
                "\n也就是你获取的分数*%scoreUpPoint% + 你通过的题目*%passedUpPoint% + 你排行上升的高度*%rankUpPoint% - 你所在的排名*%rankPoint%" +
                "\n若有好的建议，可以反馈给管理员")
                .replace("%scoreUpPoint%",scoreUpPoint + "")
                .replace("%passedUpPoint%",passedUpPoint + "")
                .replace("%rankUpPoint%",rankUpPoint + "")
                .replace("%rankPoint%",rankPoint + "")));

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

        //正常发送符合需要的人
        ForwardMessageBuilder builder = new ForwardMessageBuilder(groupList.get(0));


        for(UpdateRanker updateRanker : updateRankers){
            String msg = "";
            //积累上榜人员
            updateRankingUp(updateRanker);

            String userId = updateRanker.getUserId();
            Ranker ranker = rankerMap.get(userId);
            String name = ranker.getNickname() + "(" + userId + ")";
            boolean isMonitored = ranker.getRank() <= config.getMonitorLimit();

            int rank = updateRanker.getRank();
            if(rank >= config.getRankLimit() || (isMonitored && rank > 0)){
                int newRank = ranker.getRank();
                msg += message.getRankUp()
                        .replace("%name%",name)
                        .replace("%rankUp%", rank + "")
                        .replace("%oldRank%",(newRank + rank) + "")
                        .replace("%newRank%",newRank + "");
            }

            double score = updateRanker.getScore();
            if(score >= config.getScoreLimit() || (isMonitored && score > 0)){
                msg += message.getScoreUp()
                        .replace("%name%",name)
                        .replace("%scoreUp%",score + "");
            }

            int passed = updateRanker.getPassed();
            if(passed >= config.getPassedLimit() || (isMonitored && passed > 0)){
                msg += message.getPassedUp()
                        .replace("%name%",name)
                        .replace("%passedUp%",passed + "")
                        .replace("%passed%",ranker.getPassed() + "");
            }

            String text = updateRanker.getText();
            if (text != null){
                msg += text.replace("%name%",name) + "\n";
            }

            builder.add(config.getBot(),"CloudOJ推送", new PlainText(msg));
        }

        updateRankers.clear();

        builder.add(config.getBot(),"CloudOJ推送", new PlainText(message.getSuffix()));

        for(Group group : groupList){
            group.sendMessage(builder.build());
        }

    }
}
