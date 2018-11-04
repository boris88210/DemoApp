package com.development.borissu.demoapp.activities.contacts;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
此Activity為存取手機聯絡的的範例
 */

public class ContactActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.lv_contact_list)
    ListView mListView;

    BaseAdapter adapter;


    //定義Array，其內容為Cursor要查詢的column
    String[] FROM_COLUMS = new String[]{
            ContactsContract.Profile.DISPLAY_NAME_PRIMARY
    };
    //定義Array，
    private final static int[] TO_IDS = {
            android.R.id.text1
    };

    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    //定義projection
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
    };

    //定義＿ID的column indexes
    private static final int CONTACT_ID_INDEX = 0;
    //定義CONTACT_KEY的column index
    private static final int CONTACT_KEY_INDEX = 1;

    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //Loader的初始化
        getSupportLoaderManager().initLoader(0, null, this);

        initListView();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMS,
                TO_IDS,
                0
        );
        mListView.setAdapter(mCursorAdapter);

    }

    protected void initListView() {
        List<String> testList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String item = String.valueOf(i);
            testList.add(item);
        }

        adapter = new MyAdapter(testList);
        mListView.setAdapter(adapter);
        mListView.setDivider(null);
    }

    @OnClick(R.id.btn_get_contacts)
    public void onClickGetContacts() {


//        Cursor mProfileCursor = getContentResolver().query(
//                ContactsContract.Profile.CONTENT_URI,
//                FROM_COLUMS,
//                null,
//                null,
//                null
//        );
//        boolean b = (mProfileCursor.moveToFirst());

    }


    //---LoaderManager.CallBack---

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        mSelectionArgs[0] = "%" + mSearchString + "%";

        return new CursorLoader(ContactActivity.this,
                ContactsContract.Contacts.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    //---ListView Adapter---

    class MyAdapter extends BaseAdapter {
        List dataList;

        public MyAdapter() {
        }

        public MyAdapter(List dataList) {
            this.dataList = dataList;
        }

        public void updateListView(List dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return dataList == null ? 1 : (dataList.size() == 0 ? 1 : dataList.size());
        }

        @Override
        public Object getItem(int position) {
            return dataList == null ? null : dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_contact, parent, false);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            if (dataList == null || dataList.size() == 0) {
                vh.title.setText("查無資料");
                vh.subTitle.setText("");
            } else {
                String currentData = (String) dataList.get(position);
                vh.title.setText("Data" + currentData);
            }


            return convertView;
        }
    }

    class ViewHolder {
        @BindView(R.id.tv_text1)
        public TextView title;
        @BindView(R.id.tv_text2)
        public TextView subTitle;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }
    }


}
