package xyz.starsoc.object;

public class Ranker {
    private Double score;

    private Integer committed;

    private String name;

    private Integer rank;

    private Integer passed;

    private String userId;

    public Double getScore() {
        return this.score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getCommitted() {
        return this.committed;
    }

    public void setCommitted(Integer committed) {
        this.committed = committed;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRank() {
        return this.rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getPassed() {
        return this.passed;
    }

    public void setPassed(Integer passed) {
        this.passed = passed;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
