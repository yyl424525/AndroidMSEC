package com.example.yyl.msec.entity;

public class SaveData {

	public String time;
	public String picurl;
	public String title;
	public String contenturl;
	public boolean isCollected;


	@Override
	public String toString() {
		return "SaveData{" +
				"time='" + time + '\'' +
				", picurl='" + picurl + '\'' +
				", title='" + title + '\'' +
				", contenturl='" + contenturl + '\'' +
				'}';
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setContenturl(String contenturl) {
		this.contenturl = contenturl;
	}

	public String getTime() {

		return time;
	}

	public String getPicurl() {
		return picurl;
	}

	public String getTitle() {
		return title;
	}

	public String getContenturl() {
		return contenturl;
	}
}
