package com.example.yyl.msec.entity;

public class ReplyBean {

	private int id;					//内容ID
	private String replyAccount;	//回复人账号
	private String replyNickname;	//回复人昵称
	private String commentAccount;	//被回复人账号
	private String commentNickname;	//被回复人昵称
	private String replyContent;	//回复的内容
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReplyAccount() {
		return replyAccount;
	}
	public String getReplyNickname() {
		return replyNickname;
	}
	public void setReplyAccount(String replyAccount) {
		this.replyAccount = replyAccount;
	}
	public void setReplyNickname(String replyNickname) {
		this.replyNickname = replyNickname;
	}
	public String getCommentAccount() {
		return commentAccount;
	}
	public void setCommentAccount(String commentAccount) {
		this.commentAccount = commentAccount;
	}
	public String getCommentNickname() {
		return commentNickname;
	}
	public void setCommentNickname(String commentNickname) {
		this.commentNickname = commentNickname;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	
}
