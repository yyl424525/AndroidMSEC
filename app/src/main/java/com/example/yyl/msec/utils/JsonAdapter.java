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
import com.example.yyl.msec.entity.News;
import com.example.yyl.msec.thread.HttpImageThread;

import java.util.List;

/**
 * Created by YYL on 2016/10/30.
 */
public class JsonAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Handler handler=new Handler();
    private List<News> listnews;
    private int  loadCount=2;
    private Context context;
    public JsonAdapter(Context context, List<News> listnews){
        this.context=context;
       layoutInflater=LayoutInflater.from(context);
        this.listnews=listnews;

    }
    public int getLoadCount(){
        return loadCount;
    }
    public JsonAdapter(Context context)
    {
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    public void setData(List<News> listnews){
        this.listnews=listnews;

    }
    public void onDateChange(List<News> listnews)
    {
        loadCount++;
        this.listnews=listnews;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return listnews.size();
    }

    @Override
    public Object getItem(int position) {
        return listnews.get(position);
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
        News news=listnews.get(position);

        viewHolder.title.setText(news.getTitle());
        viewHolder.time.setText(news.getTime());
        new HttpImageThread(viewHolder.imageView,handler,news.getPicUrl()).start();

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
