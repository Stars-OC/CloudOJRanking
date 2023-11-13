package xyz.starsoc.ranking.data.Contests;

import com.google.gson.Gson;
import redis.clients.jedis.JedisPooled;
import xyz.starsoc.file.Redis;
import xyz.starsoc.object.*;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Clusters_stars
 * 用来发送竞赛排名的相关
 */
public class ContestRankMapper {
    //TODO 后面将此类和UpdateRankerMapper进行抽象拆分

    public static final ContestRankMapper INSTANCE = new ContestRankMapper();

    public static final ArrayList<UpdateRanker> mapper = new ArrayList<>();

    private final Redis redis = Redis.INSTANCE;

    private JedisPooled getRedis(){

        JedisPooled pool = null;
        String username = redis.getUsername();
        String password = redis.getPassword();
        String host = redis.getHost();
        int port = redis.getPort();

        if (username == "" || password == ""){
            pool = new JedisPooled(host, port);
        }else {
            pool = new JedisPooled(host, port, username, password);
        }

        return pool;
    }

    private String getJson(ContestRanker ranker){
        return new Gson().toJson(ranker);
    }

    private ContestRanker getRanker(String json){
        return new Gson().fromJson(json,ContestRanker.class);
    }

    /**
     * 更新排名
     * @param contestID  - 比赛ID
     * @param ranking     - 排名对象
     */
    public void updateRanking(String contestID,ContestRanking ranking){

        //TODO 因为json格式改变，现在将推迟进度

        // 比赛名称 = "Contest-" + contestID
        String name = "Contest-" + contestID;
        // 获取Redis连接
        JedisPooled pool = getRedis();
        // 获取旧排名
        Map<String, String> oldRanking = pool.hgetAll(name);
        // 遍历排名数据
        for (ContestRanker ranker : ranking.getRanking()) {

            // 获取用户名
            String username = ranker.getUsername();
            // 获取JSON字符串
            String rankerJson = getJson(ranker);

            // 如果旧排名中没有该用户名
            if (!oldRanking.containsKey(username)){
                // 将用户信息存入Redis
                pool.hset(name, username, rankerJson);
                mapper.add(new UpdateRanker(ranker));
                continue;
            }

            // 获取旧排名的JSON字符串
            String oldJson = oldRanking.get(username);
            // 将JSON字符串转换为旧排名对象
            Ranker oldRanker = getRanker(oldJson);

            // 更新旧排名对象的信息
            getUpdateRanker(oldRanker, ranker);

            // 将用户信息更新至Redis
            pool.hset(name, username,rankerJson);

        }

        // 关闭Redis连接
        pool.close();
    }


    private void getUpdateRanker(Ranker oldRanker, Ranker ranker){

        String username = ranker.getUsername();

        double score = ranker.getScore() - oldRanker.getScore();
        int oldRankerRank = oldRanker.getRank();
        int rankerRank = ranker.getRank();
        int rank =  oldRankerRank - rankerRank;
        int passed = ranker.getPassed() - oldRanker.getPassed();

        UpdateRanker updateRanker = null;
        if (rank > 0 || score > 0 || passed > 0){
            updateRanker = new UpdateRanker(username,score,rank,passed);
            mapper.add(updateRanker);
        }
    }
}
