package com.example.yyl.msec.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yyl.msec.R;
import com.example.yyl.msec.entity.ReplyBean;

import java.util.List;

public class ReplyAdapter extends BaseAdapter {

	private int resourceId;			
	private List<ReplyBean> list;		
	private LayoutInflater inflater;
	private TextView replyContent;
	private SpannableString ss;
	private Context context;
	public ReplyAdapter(Context context, List<ReplyBean> list
			, int resourceId){
		this.list = list;
		this.context = context;
		this.resourceId = resourceId;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ReplyBean bean = list.get(position);
		convertView = inflater.inflate(resourceId, null);
		replyContent = (TextView) 
					convertView.findViewById(R.id.replyContent);
		
		final String replyNickName = bean.getReplyNickname();
		final String commentNickName = bean.getCommentNickname();
		String replyContentStr = bean.getReplyContent();
		//用来标识在 Span 范围内的文本前后输入新的字符时是否把它们也应用这个效果
		//Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(前后都不包括)
		//Spanned.SPAN_INCLUSIVE_EXCLUSIVE(前面包括，后面不包括)
		//Spanned.SPAN_EXCLUSIVE_INCLUSIVE(前面不包括，后面包括)
		//Spanned.SPAN_INCLUSIVE_INCLUSIVE(前后都包括)
		ss = new SpannableString(replyNickName+"回复"+commentNickName
				+"："+replyContentStr);
		ss.setSpan(new ForegroundColorSpan(Color.BLUE),0, 
				replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new ForegroundColorSpan(Color.BLUE),replyNickName.length()+2, 
				replyNickName.length()+commentNickName.length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//为回复的人昵称添加点击事件
		ss.setSpan(new TextSpanClick(true), 0, 
				replyNickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		//为评论的人的添加点击事件
		ss.setSpan(new TextSpanClick(false),replyNickName.length()+2,
		replyNickName.length()+commentNickName.length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		replyContent.setText(ss);
		//添加点击事件时，必须设置
		replyContent.setMovementMethod(LinkMovementMethod.getInstance());
		return convertView;
	}
	
	private final class TextSpanClick extends ClickableSpan{
		private boolean status;
		public TextSpanClick(boolean status){
			this.status = status;
		}
		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(false);//取消下划线
		}
		@Override
		public void onClick(View v) {
			String msgStr = "";
			if(status){
				msgStr = "我是回复的人";
			}else{
				msgStr = "我是评论的人";
			}
			Toast.makeText(context, msgStr, Toast.LENGTH_SHORT).show();
		}
	}
	
}
