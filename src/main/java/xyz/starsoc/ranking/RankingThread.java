package xyz.starsoc.ranking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starsoc.file.Config;
import xyz.starsoc.object.ContestData;
import xyz.starsoc.ranking.data.Contests.ContestsParse;
import xyz.starsoc.ranking.data.RankingParse;
import xyz.starsoc.ranking.data.UpdateRankerMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RankingThread {

    private static Logger logger = LoggerFactory.getLogger("RankingThread");

    private static final Config config = Config.INSTANCE;
    private static final RankingParse parse = RankingParse.INSTANCE;
    private static final UpdateRankerMapper mapper = UpdateRankerMapper.INSTANCE;
    private static final ContestsParse contestsParse = ContestsParse.INSTANCE;
    private static final HashMap<String, ContestData> updateContests = ContestsParse.updateContests;

    private static int times = 0;
    private static boolean init = true;
    private static boolean timeVerify = true;

    public static void run() {

        Runnable runnable = () -> {

            if (init){
                init = false;
                mapper.init();
            }

            //logger.info("init完成");

            //用来更新日榜
            Date dateTime = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String time = simpleDateFormat.format(dateTime);
            //用来矫正时间
            if (timeVerify && "00".equals(time.split(":")[1])){
                timeVerify = false;
                times = 0;
            }

            if (time.equals(config.getRankingUpTime())){
                mapper.sendRankingUp();
            }

            if (updateContests.containsKey(time)){
                ContestData contestData = updateContests.get(time);
                contestsParse.sendMessage(contestData);
            }

            //当日排行榜推送
            if (config.getRankingUpTimeNow().contains(time)){
                mapper.sendRankingUpNow();
            }

            if(times%config.getCheckTime() == 0){
                //按照规定进行发送相关消息
                times = 0;
                if (!parse.checkRanking()){
                    return;
                }

                logger.info("获取排行榜数据成功");
                mapper.sendUpdateMessage();
            }

            if (times%config.getCheckContestTime() == 0){

                if (!contestsParse.checkUpdate()) {
                    return;
                }

                logger.info("获取竞赛数据成功 将在开始前进行提醒");
            }

            ++times;
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 20, 60, TimeUnit.SECONDS);
    }
}
