package com.cmu.project.bokennoneko.Model;

public class Score{

    private String id;
    private int maxscore;

    public Score(String id, int maxscore) {
        this.id = id;
        this.maxscore = maxscore;
    }

    public Score() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getMaxscore() {
        return maxscore;
    }

    public void setMaxscore(int maxscore) {
        this.maxscore = maxscore;
    }
}
