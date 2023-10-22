package xyz.starsoc.ranking.data.Contests;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.starsoc.file.Config;
import xyz.starsoc.object.ContestData;

import java.io.IOException;

/**
 * @author Clusters_stars
 * 用来解析竞赛信息
 */
public class ContestsParse {

    private static final Config config = Config.INSTANCE;

    public String getContests(int count) throws IOException {
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

    private ContestData getContestData(String json){
        return new Gson().fromJson(json,ContestData.class);
    }


}
