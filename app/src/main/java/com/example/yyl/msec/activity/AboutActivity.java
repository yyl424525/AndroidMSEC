package com.example.yyl.msec.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.yyl.msec.R;

import static com.example.yyl.msec.R.id.image_back;

/**
 * Created by YYL on 2016/12/6.
 */
public class AboutActivity extends Activity  implements View.OnClickListener {
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_activity);

        initView();
    }

    private void initView() {
        back= (ImageView) findViewById(image_back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.image_back:
                finish();
                break;
        }
    }
}
