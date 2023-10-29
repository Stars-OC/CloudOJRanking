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

            int count = data.getCount();
            if(count == 0){
                //初始化
                count = getContestData(getContests(1)).getCount();
            }

            Contests contests = getContestData(getContests(count));
            int newCount = contests.getCount();
            if(newCount != count){
                //判断排行榜是否更新不存在的数据
                contests = getContestData(getContests(newCount));

            }

            boolean isUpdate = getUpdate(contests);
            data.setCount(newCount);

            return isUpdate;

        } catch (IOException e) {
            return false;
        }
    }

    private void updateContestsAdd(long time,ContestData contestData){
        if (!updateContests.containsKey(time)){
            updateContests.put(time,new HashSet<>());
        }

        HashSet<ContestData> set = updateContests.get(time);
        set.add(contestData);
    }

    private boolean getUpdate(Contests contests) {

        int count = 0;

        for (ContestData contestData : contests.getData()){

            int contestID = contestData.getContestId();
//            if (!contestsMap.containsKey(contestName)){
//                updateContests.put(contestName,contestData);
//            }

            contestsMap.put(contestID,contestData);

            long nowTime = System.currentTimeMillis() / 1000;
            long startAt = contestData.getStartAt();
//            long endAt = contestData.getEndAt();
            long startTime = (long) config.getMonitorContestTime() * 60 * 1000;
            long startRemindAt = startAt * 1000 - startTime;
//            long endTime = (long) config.getMonitorContestEndTime() * 60 * 1000;
//            long endRemindAt = endAt * 1000 - endTime;

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
//            String time = simpleDateFormat.format(startTime);

            //updateContests这个队列若有多个相同时间段的需要注意
            if (!updateContests.containsKey(startRemindAt) && nowTime < startAt){
                updateContestsAdd(startRemindAt,contestData);
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

    public void sendUpMessage(ContestData contestData){

        ArrayList<Group> groupList = UpdateRankerMapper.groupList;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        String contestName = contestData.getContestName();
        String startAt = simpleDateFormat.format(contestData.getStartAt() * 1000);
        String endAt = simpleDateFormat.format(contestData.getEndAt() * 1000);

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
