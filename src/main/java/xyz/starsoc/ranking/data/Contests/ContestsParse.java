package xyz.starsoc.ranking.data.Contests;

import com.google.gson.Gson;
import net.mamoe.mirai.contact.Group;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.ContestsDataFile;
import xyz.starsoc.file.Message;
import xyz.starsoc.object.ContestData;
import xyz.starsoc.object.Contests;
import xyz.starsoc.ranking.data.UpdateRankerMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Clusters_stars
 * 用来解析竞赛信息
 */
public class ContestsParse {

    public static final ContestsParse INSTANCE = new ContestsParse();
    public static final HashMap<Long, HashSet<ContestData>> updateContests = new HashMap<>();

    private static final Config config = Config.INSTANCE;
    private static final Message message = Message.INSTANCE;
    private static final ContestsDataFile data = ContestsDataFile.INSTANCE;
    private static final Map<Integer, ContestData> contestsMap = data.getContests();


    /**
     * 根据给定的数量获取比赛数据
     * @param count 比赛数量
     * @return 比赛数据的json格式字符串，如果获取失败返回null
     * @throws IOException 如果发生I/O错误
     */
    private String getContests(int count) throws IOException {
        // 创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        // 创建Request对象，并设置请求的URL和请求方法
        Request request = new Request.Builder()
                .url(String.format(config.getUrl() + config.getContentsApi(), 1, count))
                .build();
        // 执行请求，并得到Response对象
        Response execute = client.newCall(request).execute();
        // 如果请求执行成功
        if (!execute.isSuccessful()) {
            // 返回空字符串
            return null;
        }
        // 返回响应体的数据，即比赛数据的json格式字符串
        return execute.body().string();
    }


    private Contests getContestData(String json){
        return new Gson().fromJson(json, Contests.class);
    }

    /**
     * 检查是否有更新
     * @return 如果有更新则返回true，否则返回false
     */
    public boolean checkUpdate(){
        try {
            // 获取数据总数量
            int count = data.getCount();
            if(count == 0){
                // 如果数据为空，则获取初始数量
                count = getContestData(getContests(1)).getCount();
            }

            // 获取指定数量的竞赛数据
            Contests contests = getContestData(getContests(count));
            // 获取新的数据总数量
            int newCount = contests.getCount();
            if(newCount != count){
                // 判断排行榜是否更新不存在的数据
                contests = getContestData(getContests(newCount));
            }

            // 判断是否有更新
            boolean isUpdate = getUpdate(contests);
            // 更新数据总数量
            data.setCount(newCount);

            return isUpdate;

        } catch (IOException e) {
            return false;
        }
    }



    /**
     * 更新竞赛数据
     *
     * @param time 时间
     * @param contestData 竞争数据
     */
    private void updateContestsAdd(long time, ContestData contestData) {
        // 如果更新竞争数据的map中不存在该时间对应的集合，则创建一个空集合并放入map中
        if (!updateContests.containsKey(time)) {
            updateContests.put(time, new HashSet<>());
        }

        // 获取该时间对应的竞赛数据集合
        HashSet<ContestData> set = updateContests.get(time);
        // 将竞赛数据添加到集合中
        set.add(contestData);
    }


    /**
     * 更新比赛信息
     * @param contests 比赛对象
     * @return 是否需要更新比赛信息
     */
    private boolean getUpdate(Contests contests) {

        for (ContestData contestData : contests.getData()){

            int contestID = contestData.getContestId();

            //如果有数据将重要数据进行保留
            if (contestsMap.containsKey(contestID)){
                ContestData old = contestsMap.get(contestID);
                contestData.setInviteKey(old.getInviteKey());
                contestData.setInit(old.getInit());
            }

            // 将比赛数据添加到 contestsMap 中
            contestsMap.put(contestID,contestData);

            if (contestData.getInit()){
                continue;
            }

            long nowTime = System.currentTimeMillis() / 1000 / 60;
            long startAt = contestData.getStartAt() / 60;
            long endAt = contestData.getEndAt() / 60;
            long startTime = config.getMonitorContestTime();
            long startRemindAt = startAt - startTime;
            long endTimeAt = endAt - config.getMonitorContestEndTime();

            // 如果当前时间小于比赛开始时间，将提醒时间添加到 updateContests 中
            if (nowTime < startAt){
                updateContestsAdd(startRemindAt,contestData);
                updateContestsAdd(endTimeAt,contestData);
                updateContestsAdd(startAt,contestData);
                updateContestsAdd(endAt,contestData);
                return true;
            }

            long startTimeAt = nowTime + 1;

            // 如果当前时间在比赛进行中，将提醒时间添加到 updateContests 中
            if (nowTime > startAt && nowTime < endAt){
                updateContestsAdd(startTimeAt,contestData);
                updateContestsAdd(endTimeAt,contestData);
                updateContestsAdd(endAt,contestData);
                return true;
            }
        }

        // 如果需要更新比赛信息，返回 true，否则返回 false
        return false;
    }


    /**
     * 开始前发送消息
     * @param contestData 比赛数据
     */
    public void sendUpMessage(ContestData contestData){
        sendMessages(contestData,message.getContestsUp());
    }

    /**
     * 结束前发送消息
     * @param contestData 比赛数据
     */
    public void sendDownMessage(ContestData contestData){
        sendMessages(contestData, message.getContestsDown());
    }

    private void sendMessages(ContestData contestData, String contestMsg) {
        ArrayList<Group> groupList = UpdateRankerMapper.groupList;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        // 获取比赛名称、开始时间和结束时间
        String contestName = contestData.getContestName();
        String startAt = simpleDateFormat.format(contestData.getStartAt() * 1000);
        String endAt = simpleDateFormat.format(contestData.getEndAt() * 1000);

        String inviteKey = contestData.getInviteKey();
        String inviteMsg = inviteKey != null ? "竞赛邀请码 " + inviteKey : "";
        String msg = contestMsg
                .replace("%contestName%", contestName)
                .replace("%startAt%", startAt)
                .replace("%endAt%", endAt)
                .replace("%inviteKey%", inviteMsg)
                .replace("%url%", config.getUrl())
                .replace("%languages%", "C / C++ / Java / Python");


        // 遍历分组列表，发送比赛信息
        for (Group group : groupList) {
            group.sendMessage(msg);
        }
    }

    public void sendWillUpMessage(ContestData contestData) {
        sendMessages(contestData,message.getContestsWillUp());
    }

    public void sendWillDownMessage(ContestData contestData) {
        sendMessages(contestData,message.getContestsWillDown());
    }
}
