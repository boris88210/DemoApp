package com.development.borissu.demoapp.activities.contacts;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import butterknife.BindView;

/*
此Activity為存取手機聯絡的的範例
 */

public class ContactActivity extends BaseActivity {

    @BindView(R.id.lv_contact_list)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

    }



}
