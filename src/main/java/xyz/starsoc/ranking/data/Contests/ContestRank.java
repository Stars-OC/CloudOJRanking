package xyz.starsoc.ranking.data.Contests;

import com.google.gson.Gson;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.PlainText;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.ContestsDataFile;
import xyz.starsoc.file.Message;
import xyz.starsoc.object.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static xyz.starsoc.ranking.data.UpdateRankerMapper.groupList;

/**
 * @author Clusters_stars
 * 用来提取竞赛排行的相关信息
 */
public class ContestRank {

    public static final ContestRank INSTANCE = new ContestRank();

    private Logger logger = LoggerFactory.getLogger("ContestRank");
    public static final HashSet<Integer> contestsUp = new HashSet<>();

    private final Config config = Config.INSTANCE;
    private final Message message = Message.INSTANCE;
    private final Map<Integer, ContestData> contests = ContestsDataFile.INSTANCE.getContests();

    private final ContestRankMapper mapper = ContestRankMapper.INSTANCE;
    public static final HashMap<Integer, ArrayList<UpdateRanker>> updateRankers = ContestRankMapper.updateRankers;


    /**
     * 检查是否有更新的接口
     * @param contestId 比赛ID
     * @return 如果有更新则返回true，否则返回false
     */
    public boolean checkUpdate(int contestId) {
        // 获取比赛排名信息
        ContestRanking contestRanking = getContestRank(contestId);

        if (contestRanking == null) {
            // 如果排名信息为空
            return false;
        }
        // 更新排名信息
        mapper.updateRanking(contestId, contestRanking);
        return true;
    }


    public void init(ContestData contestData) {
        // 获取竞赛的ID
        Integer contestId = contestData.getContestId();

        // 将竞赛ID添加到contestsUp集合中
        contestsUp.add(contestId);

        // 记录日志，表示开始监听竞赛排行榜数据
        logger.info("竞赛 " + contestData.getContestName() + "(" + contestId + ") 排行榜数据开始进行监听");

        // 设置contestData的init标志为true，表示已经初始化完成
        contestData.setInit(true);
    }



    /**
     * 根据竞赛ID获取竞赛排行榜
     * @param contestId 竞赛ID
     * @return 返回竞赛排行榜对象，获取失败返回null
     */
    private ContestRanking getContestRank(int contestId) {
        // 创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        // 创建Gson对象
        Gson gson = new Gson();
        // 构建请求URL
        String url = String.format(config.getUrl() + config.getContentRankApi(), contestId);
        // 创建Request对象
        Request request = new Request.Builder().url(url).build();
        try {
            // 发起请求并获取响应
            Response execute = client.newCall(request).execute();
            // 读取响应的JSON字符串
            String json = execute.body().string();
            // 将JSON字符串解析为ContestRanking对象
            ContestRanking contestRanking = gson.fromJson(json, ContestRanking.class);
            // 返回解析得到的ContestRanking对象
            return contestRanking;
        } catch (IOException e) {
            // 输出日志，表示获取竞赛排行榜失败
            logger.info("竞赛 {} 排行榜获取失败", contestId);
        }
        // 返回null
        return null;
    }


    public void sendUpdateMessage(int contestID) {
        // 检查是否包含指定比赛ID的排行榜更新信息，如果没有则返回
        if (!updateRankers.containsKey(contestID)){
            return;
        }
        // 获取指定比赛ID的排行榜更新信息列表
        ArrayList<UpdateRanker> rankers = updateRankers.get(contestID);
        // 如果排行榜更新信息列表为空，则输出提示信息并返回
        if (rankers.isEmpty()){
            logger.info("竞赛 {} 排行榜暂未更新", contestID);
            return;
        }
        // 获取当前时间及日期
        Date dateTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String today = simpleDateFormat.format(dateTime);
        // 获取指定比赛ID的竞赛数据
        ContestData contestData = contests.get(contestID);
        // 获取比赛的开始时间和结束时间
        String startAt = simpleDateFormat.format(contestData.getStartAt() * 1000);
        String endAt = simpleDateFormat.format(contestData.getEndAt() * 1000);
        // 获取比赛名称
        String contestName = contestData.getContestName();

        String msg = "";

        // 遍历群组列表
        for(Group group : groupList){
            // 创建消息转发构建器
            ForwardMessageBuilder builder = new ForwardMessageBuilder(group);
            // 添加比赛排名更新消息
            builder.add(config.getBot(),"CloudOJ竞赛推送",new PlainText(message.getPrefixContestRanking()
                    .replace("%contestName%", contestName)
                    .replace("%startAt%",startAt)
                    .replace("%endAt%",endAt)));
            // 遍历排行榜更新信息列表
            for(UpdateRanker updateRanker : rankers){
                // 发送符合需要的消息给指定用户
                String userId = updateRanker.getUserId();
                // 获取用户的昵称
                String name = updateRanker.getNickname() + "(" + userId + ")";
                // 获取用户的排名
                int rank = updateRanker.getRank();
                // 如果有排名更新，则将更新后的排名加入消息
                if(rank > 0){
                    String rankUpMsg = message.getContestRankUp()
                            .replace("%name%",name)
                            .replace("%contestName%", contestName)
                            .replace("%rankUp%", rank + "");
                    msg += rankUpMsg + "\n";
                }
                // 获取用户的得分
                double score = updateRanker.getScore();
                // 如果有得分更新，则将更新后的得分加入消息
                if(score > 0){
                    String scoreUpMsg = message.getContestScoreUp()
                            .replace("%name%",name)
                            .replace("%contestName%", contestName)
                            .replace("%scoreUp%",score + "");
                    msg += scoreUpMsg + "\n";
                }
                // 获取用户的通过题目数量
                int passed = updateRanker.getPassed();
                // 如果有通过题目数量更新，则将更新后的通过题目数量加入消息
                if(passed > 0){
                    String passedUpMsg = message.getContestPassedUp()
                            .replace("%name%",name)
                            .replace("%contestName%", contestName)
                            .replace("%passedUp%",passed + "");
                    msg += passedUpMsg + "\n";
                }
                // 获取用户的自定义消息
                String text = updateRanker.getText();
                // 如果有自定义消息，则将其加入到消息中
                if (text != null){
                    String customMsg = text.replace("%name%",name) + "\n";
                    msg += customMsg + "\n";
                }
            }
            builder.add(config.getBot(),"CloudOJ竞赛推送", new PlainText(msg));
            // 添加比赛排名更新的尾部消息
            builder.add(config.getBot(),"CloudOJ竞赛推送", new PlainText(message.getSuffixContestRankingUp()
                    .replace("%date%",today)));
            // 如果比赛有邀请码，则添加比赛邀请消息
            if (contestData.getInviteKey() != null){
                builder.add(config.getBot(),"CloudOJ竞赛推送", new PlainText(message.getContestInvited()
                       .replace("%contestName%", contestName)
                       .replace("%inviteKey%", contestData.getInviteKey())));
            }
            // 发送消息
            group.sendMessage(builder.build());
        }
        // 清空排行榜更新信息列表
        rankers.clear();
    }


}
