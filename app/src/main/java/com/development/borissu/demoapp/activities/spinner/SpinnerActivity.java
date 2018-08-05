package com.development.borissu.demoapp.activities.spinner;

import android.database.DataSetObserver;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SpinnerActivity extends BaseActivity {

//    @BindView(R.id.spinner_demo)

    private MySpinnerAdapter spinnerAdapter;

    @BindView(R.id.tv_selection)
    TextView selection;

    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spinner);
        mSpinner = findViewById(R.id.spinner_demo);
    }

    private List demoList;
    @Override
    protected void onStart() {
        super.onStart();
        demoList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String selection = "選項" + (i + 1);
            demoList.add(selection);
        }

        spinnerAdapter = new MySpinnerAdapter(this);
        spinnerAdapter.setDataList(demoList);
//        spinnerAdapter.setFooter(true);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selection.setText(demoList.get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
