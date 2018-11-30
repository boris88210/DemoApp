package com.chailease.tw.app.android.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.chailease.tw.app.android.constants.DialogConstants;

import static com.chailease.tw.app.android.constants.DialogConstants.BUTTON_STRID_DEFAULT;
import static com.chailease.tw.app.android.constants.DialogConstants.BUTTON_STRID_NONEED;

/**
 *
 */
public class DialogUtils {

    public static AlertDialog.Builder genAlertDialogBuilder(Context context
            , DialogConstants.DIALOG_TYPE dialogType, String title, String msg
            , DialogInterface.OnClickListener positiveClick, int positiveStrId
            , DialogInterface.OnClickListener negativeClick, int negativeStrId
            , DialogInterface.OnClickListener neutralClick, int neutralStrId) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg);
        int icon = android.R.drawable.ic_dialog_info;
        switch (dialogType) {
            case ALERT:
                icon = android.R.drawable.ic_dialog_alert;
                break;
            case INFO:
                icon = android.R.drawable.ic_dialog_info;
                break;
            case CONFIRM:
                icon = android.R.drawable.ic_dialog_info;
                break;
            case ERROR:
                icon = android.R.drawable.ic_dialog_alert;
                break;
        }
        if (null != positiveClick || positiveStrId!=BUTTON_STRID_NONEED) {
            if (BUTTON_STRID_DEFAULT == positiveStrId) positiveStrId = android.R.string.yes;
            dialog.setPositiveButton(positiveStrId, positiveClick);
        }
        if (null != negativeClick || negativeStrId!=BUTTON_STRID_NONEED) {
            if (BUTTON_STRID_DEFAULT == negativeStrId) negativeStrId = android.R.string.no;
            dialog.setNegativeButton(negativeStrId, negativeClick);
        }
        if (null != neutralClick || neutralStrId!=BUTTON_STRID_NONEED) {
            if (BUTTON_STRID_DEFAULT == neutralStrId) neutralStrId = android.R.string.ok;
            dialog.setNeutralButton(neutralStrId, neutralClick);
        }
        dialog.setIcon(icon);
        return dialog;
    }

}