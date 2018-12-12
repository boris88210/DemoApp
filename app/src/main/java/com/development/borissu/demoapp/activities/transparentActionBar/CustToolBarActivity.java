package com.development.borissu.demoapp.activities.transparentActionBar;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;
import com.development.borissu.demoapp.adapter.SampleAdapter;

import butterknife.BindView;

public class CustToolBarActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv_album)
    RecyclerView mAlbum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_tool_bar);

        initToolbar();

        //設定Status bar 為透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 確認取消半透明設置。
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);// 表示我們的 UI 是 LIGHT 的 style，icon 就會呈現深色系。); // 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // 跟系統表示要渲染 system bar 背景。
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    protected void initToolbar() {
        setSupportActionBar(toolbar);
        setTitle("");

    }

    @Override
    protected void onStart() {
        super.onStart();

        SampleAdapter adapter = new SampleAdapter(this);
        mAlbum.setLayoutManager(new LinearLayoutManager(this));
        mAlbum.setAdapter(adapter);
    }
}
