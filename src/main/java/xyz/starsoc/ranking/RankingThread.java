package xyz.starsoc.ranking;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.ContestsDataFile;
import xyz.starsoc.object.ContestData;
import xyz.starsoc.ranking.data.Contests.ContestRank;
import xyz.starsoc.ranking.data.Contests.ContestsParse;
import xyz.starsoc.ranking.data.RankingParse;
import xyz.starsoc.ranking.data.UpdateRankerMapper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RankingThread {

    private Logger logger = LoggerFactory.getLogger("RankingThread");

    private final Config config = Config.INSTANCE;
    private final RankingParse parse = RankingParse.INSTANCE;
    private final UpdateRankerMapper mapper = UpdateRankerMapper.INSTANCE;
    private final ContestsParse contestsParse = ContestsParse.INSTANCE;
    private final ContestRank contestRank = ContestRank.INSTANCE;
    private final Map<Integer, ContestData> contests = ContestsDataFile.INSTANCE.getContests();
    private final HashSet<Integer> contestsUp = ContestRank.contestsUp;
    private final HashMap<Long, HashSet<ContestData>> updateContests = ContestsParse.updateContests;

    private int times = -1;
    private boolean init = true;
    private boolean timeVerify = true;

    public void run() {

        Runnable runnable = () -> {

            try{

                if (init){
                    init = false;
                    mapper.init();
                }

                //logger.info("init完成");

                //用来更新日榜
                Date date = new Date();
                long dateTime = date.getTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                String time = simpleDateFormat.format(date);
                //用来矫正时间
                if (timeVerify && "00".equals(time.split(":")[1])){
                    timeVerify = false;
                    times = -1;
                }

                if (time.equals(config.getRankingUpTime())){
                    mapper.sendRankingUp();
                }

                if (updateContests.containsKey(dateTime)){

                    for (ContestData contestData : updateContests.get(dateTime)) {
                        makeEvent(dateTime,contestData);
                    }

                }

//            long remindAt = (long) config.getMonitorContestTime() * 60 * 1000;
//            long enableTime = dateTime - remindAt;
//            if (updateContests.containsKey(enableTime)){
//                //开启竞赛的排行
//                ContestData contestData = updateContests.get(enableTime);
//                if (!contestData.getStarted() || contestData.getEnded()){
//
//                }
//            }

                //当日排行榜推送
                if (config.getRankingUpTimeNow().contains(time)){
                    mapper.sendRankingUpNow();
                }

                ++times;

                if(times%config.getCheckTime() == 0 && parse.checkRanking()){
                    //按照规定进行发送相关消息

                    logger.info("获取排行榜数据成功");
                    mapper.sendUpdateMessage();

                }

                if (times%config.getCheckContestTime() == 0 && contestsParse.checkUpdate()){

                    logger.info("获取竞赛数据成功");

                }

                if (!contestsUp.isEmpty() && times%config.getCheckContestRankTime() == 0){

                    for (int contestID : contestsUp){

                        String contestName = contests.get(contestID).getContestName();
                        if (!contestRank.checkUpdate(contestID)) {
                            logger.info("竞赛 " + contestName + "(" + contestID + ") 暂未获取到排行变化数据");
                            continue;
                        }

                        logger.info("竞赛 " + contestName + "(" + contestID + ")  获取排行变化数据成功");
                    }

                }

            }catch (Exception e){
                logger.error("线程出错 {}",e.getMessage());
            }

        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 20, 60, TimeUnit.SECONDS);
    }

    public void makeEvent(long dateTime, @NotNull ContestData contestData){

        if (contestData.getEnded()){
            //结束
            contestsParse.sendDownMessage(contestData);
            updateContestsRemove(dateTime,contestData);

        }else if(contestData.getStarted() && !contestData.getEnded()){
            //已经开始 开启线程
            contestRank.init(contestData.getContestId());
            logger.info("竞赛 " + contestData.getContestName() + "(" + contestData.getContestId() + ") 排行榜数据开始进行监听");

        }else if(!contestData.getStarted()){
            //没有开始
            contestsParse.sendUpMessage(contestData);
        }

    }

    private void updateContestsRemove(long dateTime, ContestData contestData){

        HashSet<ContestData> set = updateContests.get(dateTime);
        set.remove(contestData);
        if (set.isEmpty()){
            updateContests.remove(dateTime);
        }

    }
}
