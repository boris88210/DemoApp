package com.development.borissu.demoapp.activities.table;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.activities.BaseActivity;
import com.development.borissu.demoapp.adapter.SampleAdapter;

import butterknife.BindView;

public class TableActivity extends BaseActivity {

    @BindView(R.id.rv_title)
    RecyclerView rvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
//    @BindView(R.id.scrollView)
//    ScrollView scrollView;

    SampleAdapter titleAdapter;
    SampleAdapter contentAdapter;

    boolean titleScroll;
    boolean contentScroll;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        init();
    }

    private void init() {
        rvTitle.setLayoutManager(new LinearLayoutManager(this));
        rvContent.setLayoutManager(new LinearLayoutManager(this));

        titleAdapter = new SampleAdapter(this);
        contentAdapter = new SampleAdapter(this);

        rvTitle.setAdapter(titleAdapter);
        rvContent.setAdapter(contentAdapter);

        rvTitle.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                titleScroll = true;
                if (!contentScroll) {
                    rvContent.scrollBy(dx, dy);
                }
                titleScroll = false;
            }
        });
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                contentScroll = true;

                if (!titleScroll) {
                    rvTitle.scrollBy(dx, dy);
                }

                contentScroll = false;
            }
        });


    }
}
