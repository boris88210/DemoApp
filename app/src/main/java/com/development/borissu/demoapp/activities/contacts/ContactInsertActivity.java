package com.development.borissu.demoapp.activities.contacts;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class ContactInsertActivity extends BaseActivity {

    @BindView(R.id.et_first_name)
    EditText firstNameInput;
    @BindView(R.id.et_last_name)
    EditText lastNameInput;
    @BindView(R.id.et_email)
    EditText emailInput;
    @BindView(R.id.et_phone)
    EditText phoneInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_insert);
    }

    @OnClick(R.id.btn_insert_contact)
    public void onClickInsertContact() {
        ContentValues values = new ContentValues();
// 首先向RawContacts.CONTENT_URI 執行一個空值插入(raw_contacts 表), 為了建立聯繫人 ID
        Uri rawContactUri = getContentResolver().insert(
                ContactsContract.RawContacts.CONTENT_URI, values);
// 然後取得系統返回的rawContactId ， 就是新加入的這個聯繫人的 ID
        long rawContactId = ContentUris.parseId(rawContactUri);
// 往data 表輸入姓名資料
        values.clear();
// raw_contacts_id 欄位，是 raw_contacts 表格 id 的外部鍵，用於說明此記錄屬於哪一個聯繫人
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
// Data.MIMETYPE 欄位，用於描述此資料的類型，電話號碼？Email？....
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, lastNameInput.getText().toString());
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, firstNameInput.getText().toString());
        getContentResolver().insert(
                android.provider.ContactsContract.Data.CONTENT_URI, values);
// 往data 表輸入電話資料
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneInput.getText().toString());
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        getContentResolver().insert(
                android.provider.ContactsContract.Data.CONTENT_URI, values);

//        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
//        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone_Home.getText().toString());
//        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
//        getContentResolver().insert(
//                android.provider.ContactsContract.Data.CONTENT_URI, values);
// 往data 表入Email 數據
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Email.DATA, emailInput.getText().toString());
        values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.ADDRESS);
        getContentResolver().insert(
                android.provider.ContactsContract.Data.CONTENT_URI, values);

    }
}
