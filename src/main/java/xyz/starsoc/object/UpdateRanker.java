package xyz.starsoc.object;

public class UpdateRanker {

    private String userId;
    private double score;
    private int rank;
    private String text;

    public UpdateRanker() {
    }

    public UpdateRanker(String userId,double score, int rank) {
        this.userId = userId;
        this.score = score;
        this.rank = rank;
    }

    public UpdateRanker(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
