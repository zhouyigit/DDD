package com.zy.ddd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.zy.tools.Tools;

import static android.R.attr.animation;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

//        RelativeLayout layoutWelcome=(RelativeLayout) findViewById(R.id.activity_welcome);
//        AlphaAnimation alphaAnimation=new AlphaAnimation(0.1f,1.0f);
//        alphaAnimation.setDuration(3000);
//        layoutWelcome.startAnimation(alphaAnimation);
//        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
//            public void onAnimationStart(Animation animation) {}
//            public void onAnimationRepeat(Animation animation) {}
//            public void onAnimationEnd(Animation animation) {
//                System.out.println("你好");
//                Intent intent=new Intent(WelcomeActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        //启动主页面
        Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
        startActivity(intent);
        //结束欢迎页面
        finish();

//        System.out.println(Tools.getCertificateSHA1Fingerprint(getApplicationContext()));
    }
}
