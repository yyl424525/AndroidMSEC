package com.example.yyl.msec.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.yyl.msec.R;
import com.example.yyl.msec.utils.MyFragmentPagerAdapter;
import com.example.yyl.msec.utils.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by YYL on 2016/8/7.
 */
public class FragmentHome extends Fragment {


    private List<View> viewList;
    private ViewPager viewPager;
    private ViewPagerIndicator mIndicator;
    private List<Fragment> fragmentList;
    private ImageButton image_me;
    private List<String> mTitles = Arrays.asList("学院新闻", "通知公告", "教学科研", "学工天地");


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        //初始化
        viewList = new ArrayList<>();
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mIndicator = (ViewPagerIndicator) view.findViewById(R.id.id_indicator);
        fragmentList = new ArrayList<>();


        fragmentList.add(new FragmentNews());
        fragmentList.add(new FragmentNotices());
        fragmentList.add(new FragmentScience());
        fragmentList.add(new FragmentStudents());
        //还没初始化就不能调用

        mIndicator.setVisibleTabCount(4);
        mIndicator.setTabItemTitles(mTitles);


        //设置关联的ViewPager
        mIndicator.setViewPager(viewPager, 0);


        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList);
        viewPager.setAdapter(myFragmentPagerAdapter);

        return view;
    }
}

