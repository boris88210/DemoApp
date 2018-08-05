package com.development.borissu.demoapp.activities.spinner;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.development.borissu.demoapp.R;

import java.util.List;

public class MySpinnerAdapter implements SpinnerAdapter {

    private List dataList;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private boolean isFooter;

    public MySpinnerAdapter(Context context) {
        mContext = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDataList(List dataList) {
        this.dataList = dataList;

    }

    public void setHeader(boolean isHeader) {

    }

    public void setFooter(boolean isFooter) {
        this.isFooter = isFooter;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_spinner_selection, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 取得此位置資料
        Object data = dataList.get(position);
        holder.itemName.setText(data.toString());
        return view;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {

        return dataList == null ? 0 : dataList.size();

    }

    @Override
    public Object getItem(int position) {

        return dataList == null ? null : dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.item_spinner_selection, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 取得此位置資料
        Object data = dataList.get(position);
        holder.itemName.setText(data.toString());
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    class ViewHolder {

        protected TextView itemName;

        public ViewHolder(View view) {
            itemName = view.findViewById(R.id.tv_spinner_selection_title);
        }
    }
}
