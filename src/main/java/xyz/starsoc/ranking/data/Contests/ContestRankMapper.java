package xyz.starsoc.ranking.data.Contests;

import com.google.gson.Gson;
import redis.clients.jedis.JedisPooled;
import xyz.starsoc.file.Redis;
import xyz.starsoc.object.ContestData;
import xyz.starsoc.object.Ranker;
import xyz.starsoc.object.Ranking;
import xyz.starsoc.object.UpdateRanker;

import java.util.Map;

/**
 * @author Clusters_stars
 * 用来发送竞赛排名的相关
 */
public class ContestRankMapper {
    //TODO 后面将此类和UpdateRankerMapper进行抽象拆分

    public static final ContestRankMapper INSTANCE = new ContestRankMapper();

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

    private String getJson(Ranker ranker){
        return new Gson().toJson(ranker);
    }

    private Ranker getRanker(String json){
        return new Gson().fromJson(json,Ranker.class);
    }

    public void updateRanking(String contestID, Ranking ranking){

        String name = "Contest-" + contestID;
        JedisPooled pool = getRedis();
        Map<String, String> stringStringMap = pool.hgetAll(name);
        for (Ranker ranker : ranking.getData()) {

            String json = getJson(ranker);
            pool.hset(name,ranker.getUsername(),json);

        }

        pool.close();
    }
}
