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
import com.example.yyl.msec.entity.SaveData;
import com.example.yyl.msec.thread.HttpImageThread;

import java.util.List;

/**
 * Created by YYL on 2016/10/30.
 */
public class SaveDataAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Handler handler=new Handler();
    private List<SaveData> listsavedatas;

    private Context context;
    public SaveDataAdapter(Context context, List<SaveData> listsavedatas){
        this.context=context;
       layoutInflater=LayoutInflater.from(context);
        this.listsavedatas=listsavedatas;

    }

    public SaveDataAdapter(Context context)
    {
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
    }
    public void setData(List<SaveData> listsavedatas){
        this.listsavedatas=listsavedatas;

    }
    public void onDateChange(List<SaveData> listsavedatas)
    {

        this.listsavedatas=listsavedatas;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return listsavedatas.size();
    }

    @Override
    public Object getItem(int position) {
        return listsavedatas.get(position);
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
         SaveData saveData=listsavedatas.get(position);

        viewHolder.title.setText(saveData.getTitle());
        viewHolder.time.setText(saveData.getTime());
        new HttpImageThread(viewHolder.imageView,handler,saveData.getPicurl()).start();


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
