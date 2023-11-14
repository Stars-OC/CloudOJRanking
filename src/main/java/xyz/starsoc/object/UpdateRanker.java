package xyz.starsoc.object;

public class UpdateRanker {

    /** 这是用户的Username
     */
    private String userId;

    private String nickname;
    private double score;
    private int rank;
    private int passed;
    private String text;

    public UpdateRanker() {
    }

    public UpdateRanker(Ranker ranker){
        this.userId = ranker.getUsername();
        this.score = ranker.getScore();
        this.rank = ranker.getRank();
        this.passed = ranker.getPassed();
    }
    public UpdateRanker(String userId,double score, int rank, int passed) {
        this.userId = userId;
        this.score = score;
        this.rank = rank;
        this.passed = passed;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
