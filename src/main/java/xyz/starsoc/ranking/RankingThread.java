package xyz.starsoc.ranking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starsoc.file.Config;
import xyz.starsoc.ranking.data.RankingParse;
import xyz.starsoc.ranking.data.UpdateRankerMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RankingThread {
    private static final Config config = Config.INSTANCE;
    private static final RankingParse parse = RankingParse.INSTANCE;
    private static final UpdateRankerMapper mapper = UpdateRankerMapper.INSTANCE;
    private static Logger logger = LoggerFactory.getLogger("RankingThread");
    private static int times = config.getCheckTime()-1;
    private static boolean init = true;
    public static void run() {
//        parse.checkRanking();
//        if(true){
//            return;
//        }

        Runnable runnable = () -> {

            if (init){
                init = false;
                mapper.init();
            }

            //用来更新日榜
            Date dateTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(dateTime);
            if (time.equals(config.getRankingUpTime())){
                mapper.sendRankingUp();
            }

            //当日排行榜推送
            if (config.getRankingUpTimeNow().contains(time)){
                mapper.sendRankingUpNow();
            }

            if(++times == config.getCheckTime()){
                //按照规定进行发送相关消息
                times = 0;
                if (!parse.checkRanking()){
                    return;
                }

                logger.info("获取数据成功");
                mapper.sendUpdateMessage();
            }

        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 20, 60, TimeUnit.SECONDS);
    }
}
