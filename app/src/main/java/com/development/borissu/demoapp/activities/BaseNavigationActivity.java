package com.development.borissu.demoapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.Asset.AssetActivity;
import com.development.borissu.demoapp.activities.album.AlbumActivity;
import com.development.borissu.demoapp.activities.behavior.BehaviorActivity;
import com.development.borissu.demoapp.activities.camera.CameraActivity;
import com.development.borissu.demoapp.activities.contacts.ContactActivity;
import com.development.borissu.demoapp.activities.movePic.MovePicActivity;
import com.development.borissu.demoapp.activities.spinner.SpinnerActivity;
import com.development.borissu.demoapp.activities.thread.ThreadActivity;

public class BaseNavigationActivity extends BaseActivity implements
        DrawerLayout.DrawerListener
        , NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout NavActivityLayout;
    FrameLayout mFrameLayout;
    NavigationView mNavigationView;


    protected int currentMenuItem;

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //直接取得Drawer Layout
        NavActivityLayout = (DrawerLayout) inflater.inflate(R.layout.navigation_menu, null, false);

        mFrameLayout = NavActivityLayout.findViewById(R.id.content_frame);
        mNavigationView = NavActivityLayout.findViewById(R.id.nav_view);

        NavActivityLayout.addDrawerListener(this);
        mNavigationView.setNavigationItemSelectedListener(this);
        //將Activity的內容放去FrameLayout內
        inflater.inflate(layoutResID, mFrameLayout, true);

        //設定ToolBar使其顯示Menu Icon
        Toolbar toolbar = NavActivityLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        //將組合後的畫面set給Activity
        super.setContentView(NavActivityLayout);

    }

    private void setupNavigation() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavActivityLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Drawer Listener
     *
     * @param drawerView
     * @param slideOffset
     */
    @Override
    public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerClosed(@NonNull View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    /**
     * Navigation Item Selected Listener
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent it = new Intent();
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//
        switch (item.getItemId()) {
            case R.id.nav_camera:
                it.setClass(this, CameraActivity.class);
                startActivity(it);
                break;
            case R.id.nav_recording_video:
                goToRecording();
                break;
            case R.id.nav_album:
                it.setClass(this, AlbumActivity.class);
                startActivity(it);

                break;
            case R.id.nav_spinner:
                it.setClass(this, SpinnerActivity.class);
                startActivity(it);
                break;
            case R.id.nav_listview:

                break;
            case R.id.nav_contacts:
                it.setClass(this, ContactActivity.class);
                startActivity(it);
                break;

            case R.id.nav_vibration:
                it.setClass(this, VibrationActivity.class);
                startActivity(it);

                break;

            case R.id.nav_behavior:
                it.setClass(this, BehaviorActivity.class);
                startActivity(it);
                break;
            case R.id.nav_asset:
                it.setClass(this, AssetActivity.class);
                startActivity(it);
                break;
            case R.id.nav_onTouchEvent:
                it.setClass(this, MovePicActivity.class);
                startActivity(it);
                break;
            case R.id.nav_thread:
                it.setClass(this, ThreadActivity.class);
                startActivity(it);
                break;

            case R.id.nav_setting:
                break;


            case R.id.nav_web_view:
                it.setClass(this, WebViewActivity.class);
                startActivity(it);
                break;

        }

        NavActivityLayout.closeDrawer(Gravity.START);
        return false;
    }

    protected static int REQUEST_CODE_RECORDING_VEDIO = 122;

    protected void goToRecording() {
        //設定錄影Action,Category為default
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //指定檔案位置存擋
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //TODO:使用FileProvider

        } else {

        }


        //確認有app可以處理這個intent才發動
        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, REQUEST_CODE_RECORDING_VEDIO);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RECORDING_VEDIO && resultCode == RESULT_OK) {
            Uri vedio = data.getData();
            Toast.makeText(this, "Uri: " + vedio.getPath(), Toast.LENGTH_SHORT).show();
            Log.d("TEST", "Uri: " + vedio.getPath());
        }

    }
}
