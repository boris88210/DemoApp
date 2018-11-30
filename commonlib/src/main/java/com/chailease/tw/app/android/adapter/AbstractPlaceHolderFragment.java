package com.chailease.tw.app.android.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chailease.tw.app.android.constants.DialogConstants;
import com.chailease.tw.app.android.utils.DialogUtils;

import static com.chailease.tw.app.android.constants.DialogConstants.BUTTON_STRID_NONEED;

/**
 *
 */
abstract public class AbstractPlaceHolderFragment extends Fragment {

    protected Context context;
    protected boolean usingWatermarkBackground = false;

    public AbstractPlaceHolderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  等於 Fragment 被產生時, 所以 Bundle 裡必須存在有足夠的素材資訊才能產生完整的內容
        View rootView = prepareView(inflater, container, savedInstanceState);
        initData();
        /* 設置View元件對應的linstener事件,讓UI可以與用戶產生互動 */
        setListener();
        return rootView;
    }

    @Override
    public void onStart() {
        /* 查尋資料庫或是ContentProvider,取得所需資料 */
        queryDataBase();
		/* 設定各式Adapter元件,將UI與資料做整合程現 */
        setAdapter();
        super.onStart();
    }

    @Override
    public void onResume() {
		/* 依據初始化得到的資料,若有些View元件需要重新繪制或填上預設資料,則在此處理 */
        renderUI();
        super.onResume();
    }

    @Override
    public void onStop() {
		/* 做必要的資料儲存,如存放在SharePreference或是SQL DataBase */
        saveData();
		/* 釋放DataBase相關的物件,如Cursor */
        releaseObject();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }

    @Override
    public Context getContext() {
        if (null != context) return context;
        return super.getContext();
    }

    protected void showAlertDialog(DialogConstants.DIALOG_TYPE dialogType, String title, String msg
            , DialogInterface.OnClickListener positiveClick, int positiveStrId
            , DialogInterface.OnClickListener negativeClick, int negativeStrId) {
        showAlertDialog(dialogType, title, msg, positiveClick, positiveStrId, negativeClick, negativeStrId, null, BUTTON_STRID_NONEED);
    }
    protected void showAlertDialog(DialogConstants.DIALOG_TYPE dialogType, String title, String msg
            , DialogInterface.OnClickListener positiveClick, int positiveStrId
            , DialogInterface.OnClickListener negativeClick, int negativeStrId
            , DialogInterface.OnClickListener neutralClick, int neutralStrId) {
        DialogUtils.genAlertDialogBuilder(getActivity(), dialogType, title, msg
                , positiveClick, positiveStrId
                , negativeClick, negativeStrId
                , neutralClick, neutralStrId).show();
    }

    //  onCreateView
    abstract protected View prepareView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
    protected void initData() {}
    protected void setListener() {}

    //  onStart
    protected void setAdapter() {}
    protected void queryDataBase() {}

    //  onResume
    public void renderUI() {}
    //  onStop
    protected void saveData() {}
    protected void releaseObject() {}

}