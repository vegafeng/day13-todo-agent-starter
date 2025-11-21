package com.afs.restapi.entity;

/**
 * @author FENGVE
 */
public class Reaction {
    private String emoji;
    private int count;

    public Reaction(String key, Integer value) {
        this.emoji = key;
        this.count = value;
    }

    // all getter/setter
    public String getEmoji() {
        return emoji;
    }
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;}
}
