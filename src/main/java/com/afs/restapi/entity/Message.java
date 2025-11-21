package com.afs.restapi.entity;

import jdk.jfr.DataAmount;

import java.util.List;

/**
 * @author FENGVE
 */
// message
//    -date: Date
//    -reactions: String[]
public class Message {
    private String date;
    private List<Reaction> reactions;
    // getter/setter
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public List<Reaction> getReactions() {
        return reactions;
    }
    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

}