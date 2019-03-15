package com.development.borissu.demoapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.development.borissu.demoapp.R;
import com.development.borissu.demoapp.database.entity.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DbResultAdapter extends BaseRecyclerViewAdapter {

    public DbResultAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.item_db_query_result, parent, false);

        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User currentData = (User) mDataList.get(position);
        ResultViewHolder currentViewHolder = (ResultViewHolder) holder;
        currentViewHolder.id.setText(currentData.getUid());
        currentViewHolder.firstName.setText(currentData.getFirstName());
        currentViewHolder.lastName.setText(currentData.getLastName());
        currentViewHolder.timestamp.setText(currentData.getCreateDate().toString());
    }




    class ResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_id)
        TextView id;
        @BindView(R.id.tv_first_name)
        TextView firstName;
        @BindView(R.id.tv_last_name)
        TextView lastName;
        @BindView(R.id.tv_timestamp)
        TextView timestamp;

        public ResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
