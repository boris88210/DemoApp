package com.chailease.tw.app.android.provider;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.chailease.tw.app.android.constants.ValueConstants;
import com.chailease.tw.app.android.utils.LogUtility;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;


/**
 *
 */
public class DialingInfoProvider {

    public static final String TAG_CALL =   "CALL_LOG";

    private DialingInfoProvider(){}

    /**
         *
         * @param context
         * @param phoneNumber    Phone Number
         * @param ext   Phone Ext
         * @param  closeTime    close time in millisecond
         * @return  Time in Seconds
         */
    public static int watchLastOutgoingCallTime(Context context, String phoneNumber, String ext, long closeTime) {

        Cursor managedCursor = null;
        try {
            boolean withExt = false;
            String[] mProjection = null;	// get columns
            String mSelectionClause = null;	//CallLog.Calls.NUMBER + " =  ? ";
            String[] mSelectionArgs = null;	//new String[] {eTxtPhone.getText().toString().split("#")[0] };
            if (null != phoneNumber && phoneNumber.length()>=9) {
                mSelectionClause = CallLog.Calls.TYPE + " =  " + CallLog.Calls.OUTGOING_TYPE + " AND ";
                if (StringUtils.isBlank(ext)) {
                    mSelectionClause +=  CallLog.Calls.NUMBER + " =  ? ";
                    mSelectionArgs = new String[] {phoneNumber};
                } else {
                    withExt = true;
                    mSelectionClause += CallLog.Calls.NUMBER + " LIKE  ? ";
                    mSelectionArgs = new String[] {phoneNumber + "%" };
                }
            }
            managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    mProjection, mSelectionClause, mSelectionArgs,
                    CallLog.Calls.DATE + " DESC");
            int colIdxNumber = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int colIdxCallType = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int colIdxDate = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int colIdxDuration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            while (managedCursor.moveToNext()) {
                String callNumber = managedCursor.getString(colIdxNumber);
                String callType = managedCursor.getString(colIdxCallType);
                String callDuration = managedCursor.getString(colIdxDuration);
                long callDate = managedCursor.getLong(colIdxDate);
                if (withExt) {
                    // to check the callNumber end with Ext
                    if (!callNumber.endsWith(ext) || callNumber.length()<=phoneNumber.length()) {
                        continue;
                    }
                }
                if (closeTime > 0) {
                    //  to check the call time in closing now as closeTime
                    if (System.currentTimeMillis()-closeTime>callDate) {
                        continue;
                    }
                }
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        return Integer.parseInt(callDuration);
                    case CallLog.Calls.INCOMING_TYPE:
                    case CallLog.Calls.MISSED_TYPE:
                    default:
                        return ValueConstants.INT_EMPTY;
                }
            }
        } catch (SecurityException sex) {
            LogUtility.error(DialingInfoProvider.class, "watchLastOutgoingCallTime", "to query CallLog failed by SecurityException ", sex);
        } finally {
            if (null != managedCursor && !managedCursor.isClosed())
                managedCursor.close();
        }
        return ValueConstants.INT_EMPTY;
    }

    public static String makeDialingString(String phoneNumber, String... ext) {
        if (null != ext && ext.length>0) {
            return phoneNumber+";"+ext[0];
        }
        return phoneNumber;
    }

    public void sample(Context context, String phoneNumber) {
        Cursor managedCursor = null;
        try {
//				Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
//						null, null, null);
//					managedCursor = MainActivity.this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
//							null, null, null, CallLog.Calls.DATE + " DESC");
            String[] mProjection = null;	// get columns
            String mSelectionClause = null;	//CallLog.Calls.NUMBER + " =  ? ";
            // Moves the user's input string to the selection arguments.
            String[] mSelectionArgs = null;	//new String[] {eTxtPhone.getText().toString().split("#")[0] };
            if (null != phoneNumber && phoneNumber.length()>=9) {
                //mSelectionClause = CallLog.Calls.TYPE + " =  " + CallLog.Calls.OUTGOING_TYPE + " AND " + CallLog.Calls.NUMBER + " LIKE  ? ";
                mSelectionClause = CallLog.Calls.NUMBER + " LIKE  ? ";
                mSelectionArgs = new String[] {phoneNumber.toString().split("#")[0] + "%" };
            }
            managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    mProjection, mSelectionClause, mSelectionArgs,
                    CallLog.Calls.DATE + " DESC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);
                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
            }
        } catch (SecurityException sex) {
            LogUtility.error(this, "sample", "to query CallLog failed by SecurityException ", sex);
        } finally {
            if (null != managedCursor && !managedCursor.isClosed())
                managedCursor.close();
        }
    }

}