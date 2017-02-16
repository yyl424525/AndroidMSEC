package com.example.yyl.msec.utils;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yyl.msec.R;
import com.example.yyl.msec.entity.Notice;

import java.util.List;

/**
 * Created by YYL on 2016/10/30.
 */
public class JsonNoticeAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Handler handler=new Handler();
    private List<Notice> noticelist;
    private int  loadCount=2;
    private Context context;
    public JsonNoticeAdapter(Context context, List<Notice> noticelist){
        this.context=context;
       layoutInflater=LayoutInflater.from(context);
        this.noticelist=noticelist;

    }
    public int getLoadCount(){
        return loadCount;
    }
    public JsonNoticeAdapter(Context context)
    {
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    public void setData(List<Notice> noticelist){
        this.noticelist=noticelist;

    }
    public void onDateChange(List<Notice> noticelist)
    {
        loadCount++;
        this.noticelist=noticelist;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return noticelist.size();
    }

    @Override
    public Object getItem(int position) {
        return noticelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.news_item,parent,false);

            viewHolder=new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        }
        else
        {
viewHolder= (ViewHolder) convertView.getTag();
        }
        Notice notice=noticelist.get(position);

        viewHolder.title.setText(notice.getTitle());
        viewHolder.time.setText(notice.getTime());
       // new HttpImageThread(viewHolder.imageView,handler,notice.getPicUrl()).start();

        return convertView;
    }

    class ViewHolder{
        TextView title;
        TextView contentUrl;
        TextView time;
        ImageView imageView;

        public ViewHolder(View view){
            title= (TextView) view.findViewById(R.id.title);
            time= (TextView) view.findViewById(R.id.time);
            imageView= (ImageView) view.findViewById(R.id.img);



        }

    }
}
