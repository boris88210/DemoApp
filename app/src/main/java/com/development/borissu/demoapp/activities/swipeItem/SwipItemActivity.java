package com.development.borissu.demoapp.activities.swipeItem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.adapter.SampleAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SwipItemActivity extends AppCompatActivity {

    @BindView(R.id.rv_swipableRecycerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_item);
        ButterKnife.bind(this);

        initRecyclerView();

    }


    private void initRecyclerView() {
        // 設定RecyclerView的LinearLayoutManager & adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        // 建立SwipeListHelper物件，將指定的RecyclerView和設定放入init()
        final SampleSwipeAdapter sampleAdapter = new SampleSwipeAdapter(this);
        recyclerView.setAdapter(sampleAdapter);

        SwipeEditorListHelper swipeListHelper = new SwipeEditorListHelper(recyclerView);
        swipeListHelper.setItemOnMoveListener(new SwipeEditorListHelper.ItemOnMoveListener() {
            @Override
            public void itemOnMove(int fromPos, int toPos) {
                sampleAdapter.moveItem(fromPos, toPos);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();


    }


}
