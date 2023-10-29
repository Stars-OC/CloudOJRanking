package xyz.starsoc.ranking.data.Contests;

import xyz.starsoc.file.Message;
import xyz.starsoc.object.Ranker;
import xyz.starsoc.object.Ranking;
import xyz.starsoc.object.UpdateRanker;

import java.util.HashSet;
import java.util.List;

/**
 * @author Clusters_stars
 * 用来提取竞赛排行的相关信息
 */
public class ContestRank {

    public static final ContestRank INSTANCE = new ContestRank();
    public static final HashSet<Integer> contestsUp = new HashSet<>();

    private final ContestRankMapper mapper = ContestRankMapper.INSTANCE;


    public boolean checkUpdate(int contestID) {
        return true;
    }

    public void init(int contestId) {

    }

}
