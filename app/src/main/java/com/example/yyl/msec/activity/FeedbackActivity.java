package com.example.yyl.msec.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.yyl.msec.R;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;

import static com.example.yyl.msec.R.id.image_back;

/**
 * Created by YYL on 2016/12/6.
 */
public class FeedbackActivity extends Activity implements View.OnClickListener{
    private EditText edit_title,edit_content;
    private Button btn_send;
    private ImageView back;

    private BaseAnimatorSet bas_in;
    private BaseAnimatorSet bas_out;
    public void setBasIn(BaseAnimatorSet bas_in) {
        this.bas_in = bas_in;
    }

    public void setBasOut(BaseAnimatorSet bas_out) {
        this.bas_out = bas_out;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feedback);

        initView();
    }

    private void initView() {
        bas_in = new BounceTopEnter();
        bas_out = new SlideBottomExit();
        edit_content= (EditText) findViewById(R.id.edit_content);
        edit_title= (EditText) findViewById(R.id.edit_title);
        back= (ImageView) findViewById(image_back);
        btn_send= (Button) findViewById(R.id.send);
        btn_send.setOnClickListener(this);
        back.setOnClickListener(this);

    }
    private void NormalDialogOneBtn() {
        final NormalDialog dialog = new NormalDialog(this);
        dialog.content("内容为空，重新填写吧≧▽≦")//
                .btnNum(1)
                .btnText("确定")//
                .showAnim(bas_in)//
                .dismissAnim(bas_out)//
                .show();


        dialog.setOnBtnClickL(new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                //  T.showShort(getContext(), "middle");
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.image_back:
                finish();
                break;
            case R.id.send:
                if(edit_content.getText().toString().equals(""))
                {
                    //Toast.makeText(FeedbackActivity.this,"内容为空",Toast.LENGTH_SHORT).show();
                    NormalDialogOneBtn();
                }
                else {
                    Intent data=new Intent(Intent.ACTION_SENDTO);
                    data.setData(Uri.parse("mailto:1806398352@qq.com"));
                    data.putExtra(Intent.EXTRA_SUBJECT,edit_title.getText().toString());
                    data.putExtra(Intent.EXTRA_TEXT, edit_content.getText().toString());
                    startActivity(data);
                }

              //  Toast.makeText(FeedbackActivity.this,"发送成功，感谢您的反馈",Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
