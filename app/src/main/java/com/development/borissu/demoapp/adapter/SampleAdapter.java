package com.development.borissu.demoapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.development.borissu.demoapp.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SampleAdapter extends BaseRecyclerViewAdapter {


    public SampleAdapter(Context context) {
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
        View view = mLayoutInflater.inflate(R.layout.item_demo, parent, false);
        return new DemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DemoViewHolder vh = (DemoViewHolder) holder;
        vh.name.setText(mDataList.get(position).toString());

    }


    class DemoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView name;

        public DemoViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Click" + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }


}
