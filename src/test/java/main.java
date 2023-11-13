import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import xyz.starsoc.object.ContestRanking;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
               .url("https://cloudoj.204.group/api/core/ranking/contest/11")
               .build();
        Response execute = client.newCall(request).execute();
        String json = execute.body().string();
        ContestRanking ranking = gson.fromJson(json, ContestRanking.class);
//        System.out.println(ranking.getRanking().get(0).getUid());
        System.out.println(ranking.getRanking().get(0).getDetails().get(0).getScore());
//        ranking.getRanking().forEach(s -> System.out.println(s.getUsername()));
//        System.out.println(ranking);
    }
}
