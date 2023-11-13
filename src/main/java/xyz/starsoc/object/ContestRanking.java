package xyz.starsoc.object;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.util.List;

public class ContestRanking implements Serializable {
  private Contest contest;

  private List<Integer> problemIds;

  private List<Ranking> ranking;

  public Contest getContest() {
    return this.contest;
  }

  public void setContest(Contest contest) {
    this.contest = contest;
  }

  public List<Integer> getProblemIds() {
    return this.problemIds;
  }

  public void setProblemIds(List<Integer> problemIds) {
    this.problemIds = problemIds;
  }

  public List<Ranking> getRanking() {
    return this.ranking;
  }

  public void setRanking(List<Ranking> ranking) {
    this.ranking = ranking;
  }

  public static class Contest extends Contests {

  }

  public static class Ranking extends ContestRanker{

  }
}
