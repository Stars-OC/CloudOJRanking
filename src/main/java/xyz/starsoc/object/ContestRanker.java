package xyz.starsoc.object;

import java.io.Serializable;
import java.util.List;

public class ContestRanker extends Ranker implements Serializable {
    private List<Details> details;

    public List<Details> getDetails() {
        return this.details;
    }

    public void setDetails(List<Details> details) {
        this.details = details;
    }

    public static class Details implements Serializable {
        private Integer result;

        private Double score;

        private Integer problemId;


        public Integer getResult() {
            return this.result;
        }

        public void setResult(Integer result) {
            this.result = result;
        }

        public Double getScore() {
            return this.score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public Integer getProblemId() {
            return this.problemId;
        }

        public void setProblemId(Integer problemId) {
            this.problemId = problemId;
        }

    }
}
