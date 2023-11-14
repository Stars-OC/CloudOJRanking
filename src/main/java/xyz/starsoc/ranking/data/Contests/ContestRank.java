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
        Integer contestId = contestData.getContestId();
        // 将contestId添加到contestsUp集合中
        contestsUp.add(contestId);
        logger.info("竞赛 " + contestData.getContestName() + "(" + contestId + ") 排行榜数据开始进行监听");
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
        if (!updateRankers.containsKey(contestID)){
            return;
        }
        ArrayList<UpdateRanker> rankers = updateRankers.get(contestID);
        if (rankers.isEmpty()){
            logger.info("竞赛 {} 排行榜暂未更新", contestID);
            return;
        }
        Date dateTime = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String today = simpleDateFormat.format(dateTime);
        ContestData contestData = contests.get(contestID);
        String startAt = simpleDateFormat.format(contestData.getStartAt() * 1000);
        String endAt = simpleDateFormat.format(contestData.getEndAt() * 1000);
        String contestName = contestData.getContestName();
        for(Group group : groupList){

            ForwardMessageBuilder builder = new ForwardMessageBuilder(group);
            builder.add(config.getBot(),"CloudOJ竞赛推送",new PlainText(message.getPrefixContestRanking()
                    .replace("%contestName%", contestName)
                    .replace("%startAt%",startAt)
                    .replace("%endAt%",endAt)));

            for(UpdateRanker updateRanker : rankers){

                //正常发送符合需要的人
                String msg = "";
                String userId = updateRanker.getUserId();

                String name = updateRanker.getNickname() + "(" + userId + ")";

                int rank = updateRanker.getRank();
                if(rank > 0){
                    msg += message.getContestRankUp()
                            .replace("%name%",name)
                            .replace("%contestName%", contestName)
                            .replace("%rankUp%", rank + "");
                }

                double score = updateRanker.getScore();
                if(score > 0){
                    msg += message.getContestScoreUp()
                            .replace("%name%",name)
                            .replace("%contestName%", contestName)
                            .replace("%scoreUp%",score + "");
                }

                int passed = updateRanker.getPassed();
                if(passed > 0){
                    msg += message.getContestPassedUp()
                            .replace("%name%",name)
                            .replace("%contestName%", contestName)
                            .replace("%passedUp%",passed + "");
                }

                String text = updateRanker.getText();
                if (text != null){
                    msg += text.replace("%name%",name) + "\n";
                }

                builder.add(config.getBot(),"CloudOJ竞赛推送", new PlainText(msg));
            }

            builder.add(config.getBot(),"CloudOJ竞赛推送", new PlainText(message.getSuffixContestRankingUp()
                    .replace("%date%",today)));
            if (contestData.getInviteKey() != null){
                builder.add(config.getBot(),"CloudOJ竞赛推送", new PlainText(message.getContestInvited()
                       .replace("%contestName%", contestName)
                       .replace("%inviteKey%", contestData.getInviteKey())));
            }
            group.sendMessage(builder.build());
        }
        rankers.clear();

    }

}
