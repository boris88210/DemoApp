package com.chailease.tw.app.android.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import static com.chailease.tw.app.android.constants.SystemConstants.BASE_RANDOM_STRING;

/**
 *
 */
public class CommonUtils {

    public static enum DATETYPE {
        HOUR, MINUTE, SECOND
    }

    /**
         * 獲得系統的當前時間
         *
         * @return
         */
    public static String getCurTime() {
        SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return dateFormate.format(date);
    }

    /**
         * @param begin    開始時間
         * @param end      當前時間
         * @param datetype 回傳差單位
         * @return 兩個時間點之間的差
         */
    public static long compareTime(String begin, String end, DATETYPE datetype) {
        SimpleDateFormat dateFormate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long result = 0;

        Date date1;
        Date date2;
        try {
            date1 = dateFormate.parse(begin);
            date2 = dateFormate.parse(end);
            long diff = date2.getTime() - date1.getTime();
            switch (datetype) {
                case HOUR:
                    //小時
                    result = diff / (1000 * 60 * 60);
                    break;
                case MINUTE:
                    result = diff / (1000 * 60);
                    break;
                case SECOND:
                    result = diff / (1000);
                    break;
            }
        } catch (ParseException e) {
            LogUtility.error(CommonUtils.class, "compareTime", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 產生亂數字串，未超過基本字元數時不重覆字元
     *
     * @param len 字串長度
     * @return 亂數字串
     */
    public static String getRandomString(int len) {
        String strBaseRandomWord = BASE_RANDOM_STRING;//基本亂數字串中包含字元定義
        String strResult;

        /** 將基本字元先做一次亂數位置重排 **/
        String strRandomWord;
        StringBuilder sb = new StringBuilder();
        int rnd;
        int wordLen = strBaseRandomWord.length();

        HashSet rndSet = new HashSet<Integer>(wordLen);
        for (int i = 0; i < wordLen; i++) {
            rnd = (int) (wordLen * Math.random());
            while (!rndSet.add(rnd))
                rnd = (int) (wordLen * Math.random());
            sb.append(strBaseRandomWord.charAt(rnd));
        }

        strRandomWord = sb.toString(); //得到新的排列字元組合

        /** 使用新字元組合產生特定長度字串 **/
        sb = new StringBuilder();
        if (len > wordLen) {
            //字元會重覆
            for (int i = 0; i < len; i++) {
                int idx = (int) (wordLen * Math.random());
                sb.append(strRandomWord.charAt(idx));
            }
        } else {
            //字元不重覆
            rndSet = new HashSet<Integer>(wordLen);
            for (int i = 0; i < len; i++) {
                rnd = (int) (wordLen * Math.random());
                while (!rndSet.add(rnd))
                    rnd = (int) (wordLen * Math.random());
                sb.append(strRandomWord.charAt(rnd));
            }
        }
        strResult = sb.toString();

        return strResult;
    }

    /**
     * default String
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static String getString(Object value, String defaultValue) {
        if (null != value) {
            return value.toString();
        }
        return defaultValue;
    }

//    /**
//     * @param obj
//     * @param attrName
//     * @return
//     * @deprecated change to {@link JSONUtility#getJSONString(JSONObject, String)}
//     */
//    public static String getJSONString(JSONObject obj, String attrName) {
//        return JSONUtility.getJSONString(obj, attrName);
//    }
//
//    /**
//     * @param obj
//     * @param attrName
//     * @return
//     * @deprecated change to {@link JSONUtility#getJSONInt(JSONObject, String)}
//     */
//    public static int getJSONInt(JSONObject obj, String attrName) {
//        return JSONUtility.getJSONInt(obj, attrName);
//    }

//    public static Object putCommonCache(String key, Object cacheItem) {
//        return commonCache.put(key, cacheItem);
//    }
//
//    public static Object getCommonCache(String key) {
//        return commonCache.get(key);
//    }
//
//    public static Object removeCommonCache(String key) {
//        if (commonCache.containsKey(key))
//            return commonCache.remove(key);
//        return null;
//    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static String processInputStream(InputStream mInputStream) throws IOException {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer();
        String line;
        reader = new BufferedReader(new InputStreamReader(mInputStream));
        while ((line = reader.readLine()) != null) {
            data.append(line);
        }
        reader.close();
        return data.toString();
    }

    public static void BaseInputMethodManager(Activity activity) throws Exception {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        //隱藏
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


}