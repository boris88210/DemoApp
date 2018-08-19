package com.development.borissu.demoapp.activities.Asset;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

        readAssetFile("SampleJson.json");


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
}
