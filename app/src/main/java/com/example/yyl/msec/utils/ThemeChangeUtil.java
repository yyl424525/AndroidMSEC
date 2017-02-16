package com.example.yyl.msec.utils;

import android.app.Activity;

import com.example.yyl.msec.R;


public class ThemeChangeUtil {
    public static boolean isChange = false;
    public static void changeTheme(Activity activity){
        if(isChange){
            activity.setTheme(R.style.NightTheme);
        }
    }
}
