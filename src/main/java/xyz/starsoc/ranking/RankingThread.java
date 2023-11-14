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
                long dateTime = date.getTime() / 1000 / 60;
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

//                System.out.println(dateTime);
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
                            logger.info("竞赛 " + contestName + "(" + contestID + ") 获取排行变化数据失败");
                            continue;
                        }

                        logger.info("竞赛 " + contestName + "(" + contestID + ")  获取排行变化数据成功");
                        contestRank.sendUpdateMessage(contestID);
                    }

                }

            }catch (Exception e){
                logger.error("线程出错 {}",e.getMessage());
            }

        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 20, 60, TimeUnit.SECONDS);
    }

    /**
     * 创建事件
     *
     * @param dateTime 事件日期时间
     * @param contestData 竞赛数据
     */
    public void makeEvent(long dateTime, @NotNull ContestData contestData){

        if (contestData.getEnded() && contestData.getInit()){
            // 如果竞赛已经结束，则发送消息通知相关操作
            contestsParse.sendDownMessage(contestData);
            updateContestsRemove(dateTime,contestData);

        }else if(contestData.getStarted() && !contestData.getEnded() && !contestData.getInit()){
            // 如果竞赛已经开始，则初始化竞赛排名并开始监听
            contestRank.init(contestData);


        }else if(!contestData.getStarted()){
            // 如果竞赛还没有开始，则发送消息通知相关操作
            contestsParse.sendUpMessage(contestData);
        }

    }


    /**
     * 更新指定时间的竞赛集合，将指定的竞赛数据从集合中移除
     * @param dateTime 移除的时间点
     * @param contestData 移除的竞赛数据
     */
    private void updateContestsRemove(long dateTime, ContestData contestData){
        // 从更新竞赛集合中获取指定时间的竞赛集合
        HashSet<ContestData> set = updateContests.get(dateTime);
        // 从竞赛集合中移除指定的竞赛数据
        set.remove(contestData);
        // 如果竞赛集合为空，则从更新竞赛集合中移除该时间点的竞赛集合
        if (set.isEmpty()){
            updateContests.remove(dateTime);
        }
    }

}
