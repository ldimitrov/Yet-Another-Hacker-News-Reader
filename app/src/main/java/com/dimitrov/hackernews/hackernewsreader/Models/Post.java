package com.dimitrov.hackernews.hackernewsreader.Models;

import java.util.ArrayList;

public class Post {
    private int number;
    private long id;
    private ArrayList<String> kids;
    private long time;
    private String title;
    private String url;
    private String prettyUrl;

    public Post(Long id, int index) {
        this.id = id;
        this.number = index;
    }

    public int getNumber() {
        return number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<String> getKids() {
        return kids;
    }

    public void setKids(ArrayList<String> kids) {
        this.kids = kids;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getPrettyUrl() {
        return prettyUrl;
    }

    public void setPrettyUrl(String prettyUrl) {
        this.prettyUrl = prettyUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
