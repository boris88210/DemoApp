package com.development.borissu.demoapp.activities.swipeItem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.adapter.BaseRecyclerViewAdapter;
import com.development.borissu.demoapp.utils.DeviceInfo;
import com.development.borissu.demoapp.utils.LogUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class SampleSwipeAdapter extends BaseRecyclerViewAdapter<SampleSwipeAdapter.SwipeViewHolder> {


    public SampleSwipeAdapter(Context context) {
        super(context);
        mDataList = new ArrayList<String>();

        for (int i = 0; i < 20; i++) {
            String s = String.valueOf(i + 1);
            mDataList.add(s);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = SwipeEditorListHelper.createSwipeView(mLayoutInflater, parent, R.layout.item_swipe_list);
        return new SwipeViewHolder(view, parent);
    }


    @Override
    protected void onBinSwipeViewHolder(SwipeViewHolder viewHolder, int position) {
        String currentData = (String) mDataList.get(position);

        viewHolder.text.setText(currentData);
    }

    // 移動畫面與資料
    public void moveItem(int fromPos, int toPos) {
        Collections.swap(mDataList, fromPos, toPos);
        notifyItemMoved(fromPos, toPos);
    }


    class SwipeViewHolder extends SwipeEditorListHelper.SwipeableViewHolder {

        @BindView(R.id.tv_text1)
        public TextView text;

        public SwipeViewHolder(View itemView, ViewGroup viewGroup) {
            super(itemView, viewGroup);
//            text = itemView.findViewById(android.R.id.text1);
//            actionWidth = getResources().getDimensionPixelSize(R.dimen.default_action_size);
            actionWidth = DeviceInfo.getDpToPx(mContext, 80);
            // 設定自己的Action按鈕（右邊）
            final ImageButton btnRight = (ImageButton) mLayoutInflater.inflate(R.layout.action_right, vActionRight, false);

            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtility.debug("right onClick = " + getAdapterPosition());
//                    adapter.removeItem(getAdapterPosition());
                }
            });
            addRightAction(btnRight);

            // 測試第二顆按鈕
//            final ImageButton btnRight2 = (ImageButton) layoutInflater.inflate(R.layout.action_right, vActionRight, false);
//            btnRight2.setBackgroundColor(Color.BLUE);
//            btnRight2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "right onClick = " + getAdapterPosition());
//                    adapter.removeItem(getAdapterPosition());
//                }
//            });
//            addRightAction(btnRight2);

            // 設定自己的Action按鈕（左邊）
            final Button btnLeft = (Button) mLayoutInflater.inflate(R.layout.action_left, vActionLeft, false);
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtility.debug("left onClick  = " + getAdapterPosition());
//                    adapter.changeItemMode(getAdapterPosition());
                }
            });
            addLeftAction(btnLeft);

            // 測試第二顆按鈕
//            final Button btnLeft2 = (Button) layoutInflater.inflate(R.layout.action_left, vActionLeft, false);
//            btnLeft2.setBackgroundColor(Color.YELLOW);
//            btnLeft2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "left onClick  = " + getAdapterPosition());
//                    adapter.changeItemMode(getAdapterPosition());
//                }
//            });
//            addLeftAction(btnLeft2);
        }
    }


}
