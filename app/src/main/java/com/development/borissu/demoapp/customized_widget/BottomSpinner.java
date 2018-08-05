package com.development.borissu.demoapp.customized_widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.development.borissu.demoapp.R;

public class BottomSpinner extends android.support.v7.widget.AppCompatTextView {

    PopupWindow spinner;

    public BottomSpinner(final Context context, AttributeSet attrs) {
        super(context, attrs);
        final Activity a = (Activity)context;
        final View root = a.findViewById(android.R.id.content).getRootView();
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:顯示spinner
                Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();
                spinner.showAtLocation(root, Gravity.BOTTOM, 0, 0);
            }
        });


        spinner = new PopupWindow(context);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.bottom_spinner, null);
        spinner.setContentView(view);
        spinner.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        spinner.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        spinner.isFocusable();
    }

    public BottomSpinner(final Context context) {
        super(context);

    }

    public void setSpinnerTitle(String title) {

    }

    public void setSpinnerCancelBtn(String text, OnClickListener onClickListener) {

    }

    public void showSpinner() {

    }


}
