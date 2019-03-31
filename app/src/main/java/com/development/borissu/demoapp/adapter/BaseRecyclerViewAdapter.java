package com.development.borissu.demoapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    protected List mDataList;
    protected boolean hasHeader;
    protected boolean hasFooter;
    protected int TYPE_HEADER = -1;
    protected int TYPE_FOOTER = -2;
    protected int TYPE_DATA = 0;
    protected View headerView;
    protected View footerView;


    public BaseRecyclerViewAdapter(Context context) {
        super();
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataList(List dateList) {
        mDataList = dateList;
        notifyDataSetChanged();
    }

    public void setHeader(View view) {
        if (null == view) {
            hasHeader = false;
            headerView = null;
        } else {
            hasHeader = true;
            headerView = view;
        }
    }

    public void setFooter(View view) {
        if (null == view) {
            hasFooter = false;
            footerView = null;
        } else {
            hasFooter = true;
            footerView = view;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (hasHeader) {
            if (position == 0) {
                return TYPE_HEADER;
            }
            if (position == mDataList.size() + 1) {
                return TYPE_FOOTER;
            }
        }
        if (hasFooter && position == mDataList.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        if (null == mDataList) {
            return 0;
        }
        if (hasHeader) {
            if (hasFooter) {
                return mDataList.size() + 2;
            }
            return mDataList.size() + 1;
        }
        if (hasFooter) {
            return mDataList.size() + 1;
        }
        return mDataList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        onBinSwipeViewHolder((T) holder, position);
    }

    protected void onBinSwipeViewHolder(T viewHolder, int position) {

    }
}
