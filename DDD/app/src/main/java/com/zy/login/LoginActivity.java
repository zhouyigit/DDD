package com.zy.login;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.zy.ddd.R;


public class LoginActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.anim_static, R.anim.anim_outer_to_bottom);
    }
}