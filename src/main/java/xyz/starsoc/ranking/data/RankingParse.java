package xyz.starsoc.ranking.data;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.Data;
import xyz.starsoc.object.Ranker;
import xyz.starsoc.object.Ranking;
import xyz.starsoc.object.UpdateRanker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankingParse {
    public static final RankingParse INSTANCE = new RankingParse();

    private static final ArrayList<UpdateRanker> mapper = UpdateRankerMapper.updateRankers;

    private static final Config config = Config.INSTANCE;
    private static final Data data = Data.INSTANCE;
    private static final Map<String, Ranker> rankerMap = data.getPersons();


    //private final String rankUrl = config.getUrl() + config.getRankingApi();

    public String getRanks(int count) throws IOException {
        //获取json格式的数据
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(String.format(config.getUrl() + config.getRankingApi(),1,count))
                .build();
        Response execute = client.newCall(request).execute();
        if(!execute.isSuccessful()){
            return null;
        }

        return execute.body().string();
    }

    public Ranking getRanking(String json){
        return new Gson().fromJson(json, Ranking.class);
    }

    public boolean checkRanking(){
        //检测是否进行一定的更新

        try {

            int count = data.getCount();
            if(count == 0){
                //初始化
                count = getRanking(getRanks(1)).getCount();
            }

            Ranking ranking = getRanking(getRanks(count));
            int newCount = ranking.getCount();
            if(newCount != count){
                //判断排行榜是否更新不存在的数据
                ranking = getRanking(getRanks(newCount));

            }

            boolean updateRanking = updateRanking(ranking);
            data.setCount(newCount);

            return updateRanking;

        } catch (IOException e) {
            return false;
        }
    }

    public void getUpdateRanker(Ranker ranker){
        //TODO 后面使用redis来缓存
        boolean isNew = false;
        String userId = ranker.getUserId();
        if(!rankerMap.containsKey(userId)){
            //用来存储没有的数据
            rankerMap.put(userId,ranker);
            isNew = true;
        }

        Ranker oldRanker = rankerMap.get(userId);
        if(isNew){
            //筛选新人对于其限制
            oldRanker.setRank(data.getCount());
            oldRanker.setScore(0.0);
        }

        double score = ranker.getScore() - oldRanker.getScore();
        int oldRankerRank = oldRanker.getRank();
        int rankerRank = ranker.getRank();
        int rank =  oldRankerRank - rankerRank;

        //更新数据
        rankerMap.put(userId,ranker);

        //将数据进行判断，然后加入相对应的队列中
        UpdateRanker updateRanker = new UpdateRanker(userId);
        if((oldRankerRank <= config.getMonitorLimit() || rankerRank <= config.getMonitorLimit()) && (rank > 0 || score > 0)){
            //如果在检测范围内将实时报告其状态  后面看情况是否进行退步的变更
            updateRanker.setRank(rank);
            updateRanker.setScore(score);
            mapper.add(updateRanker);
            return;
        }

        int isChanging = 0;
        //用来判断是否能够加入mapper中
        if(score > config.getScoreLimit()){
            updateRanker.setScore(score);
            ++isChanging;
        }

        if(rank > config.getRankLimit()){
            updateRanker.setRank(rank);
            ++isChanging;
        }

        if(isChanging > 0){
            mapper.add(updateRanker);
        }
    }

    public boolean updateRanking(Ranking ranking){
        //用来更新数据用的 将有的进行比较，没有的进行更新
        List<Ranking.Data> list = ranking.getData();
        int size = list.size();
        for(int i = size - 1;i >= 0;i--){
            //从后往前降低资源消耗
            Ranker ranker = list.get(i);

            if(data.getCount() == 0){
                //初始化
                rankerMap.put(ranker.getUserId(),ranker);
                continue;
            }

            getUpdateRanker(ranker);

        }
        return true;
    }
}
