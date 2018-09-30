package com.development.borissu.demoapp.activities.behavior;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class VerticalNestedRecyclerView extends RecyclerView {

    public VerticalNestedRecyclerView(Context context) {
        super(context);
    }

    public VerticalNestedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalNestedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean startNestedScroll(int axes) {
        Log.d("NestedRecyclerView", "startNestedScroll");
        return super.startNestedScroll(axes);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        Log.d("NestedRecyclerView", "onStartNestedScroll");

        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }
}
