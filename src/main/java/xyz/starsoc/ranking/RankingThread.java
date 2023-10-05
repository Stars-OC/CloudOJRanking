package xyz.starsoc.ranking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.Data;
import xyz.starsoc.object.Ranker;
import xyz.starsoc.ranking.data.RankingParse;
import xyz.starsoc.ranking.data.UpdateRankerMapper;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RankingThread {
    private static final Config config = Config.INSTANCE;
    private static final RankingParse parse = RankingParse.INSTANCE;
    private static final UpdateRankerMapper mapper = UpdateRankerMapper.INSTANCE;
    private static Logger logger = LoggerFactory.getLogger("RankingThread");
    public static void run() {
//        parse.checkRanking();
//        if(true){
//            return;
//        }
        Runnable runnable = () -> {
            if (!parse.checkRanking()){
                return;
            }

            logger.info("获取json成功，正在发送中...");
//            Ranker ranker = new Ranker();
//            ranker.setRank(123);
//            ranker.setUserId("test");
//
//            Map<String, Ranker> persons = Data.INSTANCE.getPersons();
//            System.out.println(persons.isEmpty());
//            persons.put("test",ranker);
//            System.out.println(persons.size());
//            System.out.println(persons.get("test").getUserId());
            mapper.sendMessage();
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 20, config.getTime(), TimeUnit.SECONDS);
    }
}
