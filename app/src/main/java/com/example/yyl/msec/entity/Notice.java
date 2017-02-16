package com.example.yyl.msec.entity;

/**
 * Created by YYL on 2016/10/30.
 */
public class Notice {
    public int id;
    public String title;
    public String contentUrl;
    public String time;


    public Notice(String time, String title, String contentUrl){
        super();
        this.time=time;

        this.contentUrl=contentUrl;
        this.title=title;
    }
    public Notice(){

    }
    public int getId(){return  id;}
    public void setId(int id){
        this.id=id;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}