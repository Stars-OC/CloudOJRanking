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


    private String getContests(int count) throws IOException {
        //获取json格式的数据
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(config.getUrl() + config.getContentsApi(),1,count))
                .build();
        Response execute = client.newCall(request).execute();
        if(!execute.isSuccessful()){
            return null;
        }

        return execute.body().string();
    }

    private Contests getContestData(String json){
        return new Gson().fromJson(json, Contests.class);
    }

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


    private boolean getUpdate(Contests contests) {

        int count = 0;

        for (ContestData contestData : contests.getData()){

            int contestID = contestData.getContestId();
//            if (!contestsMap.containsKey(contestName)){
//                updateContests.put(contestName,contestData);
//            }

            //后面逻辑进行重写 进行hashcode校验对象
            if (contestData.getInit()){
                continue;
            }
            contestsMap.put(contestID,contestData);

            long nowTime = System.currentTimeMillis() / 1000 / 60;
            long startAt = contestData.getStartAt() / 60;
            long endAt = contestData.getEndAt() / 60;
            long startTime = config.getMonitorContestTime();
            long startRemindAt = startAt - startTime;
            long endTimeAt = endAt - config.getMonitorContestEndTime();
//            long endTime = (long) config.getMonitorContestEndTime() * 60 * 1000;
//            long endRemindAt = endTime * 1000 - endTime;

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//            String time = simpleDateFormat.format(startTime);

            //updateContests这个队列若有多个相同时间段的需要注意
            if (nowTime < startAt){
                updateContestsAdd(startRemindAt,contestData);
                ++count;
            }

            long startTimeAt = nowTime + config.getCheckContestRankTime();
//            if (!contestData.getEnded()){
//                System.out.println(nowTime);
//                System.out.println(endAt);
//                System.out.println(startTimeAt);
//            }
            //若在比赛也要进行
            if (nowTime > startAt && nowTime < endAt){

                updateContestsAdd(startTimeAt,contestData);
//                System.out.println(endTimeAt);
                updateContestsAdd(endTimeAt,contestData);
                ++count;
            }

            //endTime将在新加队列里进行提醒
//            if (!updateContests.containsKey(endRemindAt) && nowTime > startAt && nowTime < endAt){
//                updateContests.put(endRemindAt,contestData);
//                ++count;
//            }

        }

        return count > 0;
    }

    /**
     * 向群聊发送消息
     *
     * @param contestData 比赛数据
     */
    public void sendUpMessage(ContestData contestData){

        // 获取分组列表
        ArrayList<Group> groupList = UpdateRankerMapper.groupList;
        // 创建日期格式对象
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        // 获取比赛名称、开始时间和结束时间
        String contestName = contestData.getContestName();
        String startAt = simpleDateFormat.format(contestData.getStartAt() * 1000);
        String endAt = simpleDateFormat.format(contestData.getEndAt() * 1000);

        // 遍历分组列表，发送比赛信息
        for (Group group : groupList){
            group.sendMessage(message.getContestsUp()
                    .replace("%contestName%",contestName)
                    .replace("%startAt%",startAt)
                    .replace("%endAt%",endAt)
                    .replace("%languages%","C / C++ / Java / Python"));
        }
    }


    public void sendDownMessage(ContestData contestData){

        ArrayList<Group> groupList = UpdateRankerMapper.groupList;

    }

}
