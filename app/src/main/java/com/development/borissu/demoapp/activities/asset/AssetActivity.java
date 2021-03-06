package com.development.borissu.demoapp.activities.asset;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class AssetActivity extends BaseActivity {

    //TODO:讀取Asset的json檔案
    //TODO:存到內部空間

    @BindView(R.id.file_content)
    TextView fileContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);


    }

    @Override
    protected void onStart() {
        super.onStart();

//        readAssetFile("SampleJson.json");
//        DataObject dataObject = getNewDataObject();

//        saveFile(getNewDataObjectList());
//        saveFile(getNewDataObjectArray());

//        try {
//            InputStreamReader reader = new InputStreamReader(openFileInput("test.json"));
//
//            Gson g = new Gson();
//            DataObject[] d = g.fromJson(reader, DataObject[].class);
//
//            fileContent.setText(d[0].toString() + "\n" + d[1].toString());
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


    }

    private String readAssetFile(String fileName) {
        String result = "";
        try {
            InputStreamReader reader = new InputStreamReader(getAssets().open(fileName));
            BufferedReader bufferedReader = new BufferedReader(reader);

            Byte[] buffer = new Byte[1024];


        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //取得路徑
    private File getStoragePath() {
        File f = getFilesDir();

        return f;
    }


    private void saveFile(Object... dataObject) {
        String fileName = "test.json";
        File directory = getStoragePath();
        FileOutputStream fileOutputStream;

        Gson gson = new Gson();
        String content = "";
        if (dataObject.length == 1) {
            content = gson.toJson(dataObject[0]);
        } else {
            content = gson.toJson(dataObject);
        }
        Log.d("JSON_String", content);

        if (directory.canWrite()) {
            try {
                fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                fileOutputStream.write(content.getBytes());
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            File textFile = new File(directory, "Test.json");


        }
    }

    private DataObject getNewDataObject() {
        DataObject object = new DataObject();
        object.account = "Account";
        object.userName = "Boris";
        object.userNumber = 12345;
        return object;
    }

    private List<DataObject> getNewDataObjectList() {

        List<DataObject> list = new ArrayList<>();
        for (int i = 0; i < 0; i++) {
            list.add(getNewDataObject());
        }
        return list;
    }

    private DataObject[] getNewDataObjectArray() {

        DataObject[] array = new DataObject[1];
        for (int i = 0; i < 1; i++) {
            array[i] = getNewDataObject();

        }
        return array;
    }

    class DataObject {
        String account;
        String userName;
        int userNumber;

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("Account: ");
            sb.append(account);
            sb.append(", \n");
            sb.append("User Name: ");
            sb.append(userName);
            sb.append(", \n");
            sb.append("User Number: ");
            sb.append(userNumber);
            return sb.toString();
        }
    }
}
