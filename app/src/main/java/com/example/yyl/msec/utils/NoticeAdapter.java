package com.example.yyl.msec.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yyl.msec.R;
import com.example.yyl.msec.entity.Notice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YYL on 2016/10/30.
 */
public class NoticeAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater layoutInflater;
    private Handler handler=new Handler();
    private List<Notice> noticelist;
    private List<Notice> new_listnotice=new ArrayList<>();
    private int  loadCount=2;
    private MyFilter mFilter;
    private Context context;
    public NoticeAdapter(Context context, List<Notice> noticelist){
        this.context=context;
       layoutInflater=LayoutInflater.from(context);
        this.noticelist=noticelist;

    }
    public int getLoadCount(){
        return loadCount;
    }
    public NoticeAdapter(Context context)
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

    @Override
    public Filter getFilter() {
        if (null == mFilter) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }
    class MyFilter extends Filter {
        @Override
        // 该方法在子线程中执行
        // 自定义过滤规则
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            List<String> newValues = new ArrayList<String>();
            List<String> oldValues = new ArrayList<String>();
            String filterString = constraint.toString().trim()
                    .toLowerCase();
            for(int i=0;i<noticelist.size();i++){
                oldValues.add(noticelist.get(i).title);
            }

            // 如果搜索框内容为空，就恢复原始数据
            if (TextUtils.isEmpty(filterString)) {
                newValues=oldValues;

            } else {
                // 过滤出新数据
//                for (String str : oldValues) {
//                    if (-1 != str.toLowerCase().indexOf(filterString)) {
//                        newValues.add(str);
//                    }
//                }

                for(int i=0;i<noticelist.size();i++)
                {
                    if(-1!=noticelist.get(i).title.toString().toLowerCase().indexOf(filterString))
                    {
                        new_listnotice.add(noticelist.get(i));
                        noticelist.remove(i);
                    }
                }
            }

//            results.values = newValues;
//            results.count = newValues.size();
            results.count=new_listnotice.size();
            //     System.out.println("new_listnews:"+new_listnews.size());
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //  mData = (List<String>) results.values;
            for(int i=0;i<results.count;i++){

                if(!issave(new_listnotice.get(i).title))
                {
                    noticelist.add(0,new_listnotice.get(i));
                }

            }
            if (results.count > 0) {
                notifyDataSetChanged();// 通知数据发生了改变
            } else {
                notifyDataSetInvalidated(); // 通知数据失效
            }
        }

    }
    /**
     * 判断是否已保存过
     * @return
     */
    public  boolean issave(String title)
    {
        boolean flag=false;
        for(Notice notice:noticelist)
        {
            if(notice.title.equals(title))
            {
                flag=true;
                break;
            }
        }
        return flag;
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
