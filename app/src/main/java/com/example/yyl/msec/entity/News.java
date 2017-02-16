package com.example.yyl.msec.entity;

/**
 * Created by YYL on 2016/10/30.
 */
public class News {
    public int id;
    public String title;
    public String contentUrl;
    public String time;
    public String picUrl;
    public boolean isCollected=false;



    public News(String time, String title, String contentUrl, String picUrl){
        super();
        this.time=time;
        this.picUrl=picUrl;
        this.contentUrl=contentUrl;
        this.title=title;
    }
    public News(){

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

    public String getPicUrl() {
        return picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
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