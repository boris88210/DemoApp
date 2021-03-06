package com.development.borissu.demoapp.utils;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 取得Device相關資訊
 */
public class DeviceInfo {

    /**
     * 取得手機的 Android_id
     *
     * @return ANDROID_ID
     */
    public static String getANDROID_ID(Context content) {
        return Settings.Secure.getString(content.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 取得手機的 android SDK 版本
     *
     * @return android SDK版本
     */
    public static int getSDK_INT() {
        return Build.VERSION.SDK_INT;
    }

    public static String getDEVICE_ID() {
        return getSerial();
    }

    /**
     * 手機型號
     *
     * @return 手機型號
     */
    public static String getPHONE_MODEL() {
        return Build.MODEL;
    }

    static PackageInfo packageInfo = null;

    /**
     * 取得android SDK 版本
     *
     * @return android SDK版本
     */
    public static int getSDK() {
        return getSDK_INT();
    }

    /**
     * 手機型號
     *
     * @return 手機型號
     */
    public static String getPhoneModel() {
        return getPHONE_MODEL();
    }

    /**
     * 硬體的唯一值:該值必須要API Level 9才支援
     * API level 26 以上需要權限READ_PHONE_STATE
     *
     * @return 硬體的唯一值
     */
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getSerial() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            return Build.getSerial();
        } else {
            return Build.SERIAL;
        }
    }

    /**
     * 製造商名稱
     *
     * @return 製造商名稱
     */
    @TargetApi(Build.VERSION_CODES.DONUT)
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * 手機的主機板名稱
     *
     * @return 主機板名稱
     */
    public static String getBoard() {
        return Build.BOARD;
    }

    /**
     * 手機的手機的品牌名稱
     *
     * @return 品牌名稱
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 取得頁面寬
     *
     * @param context
     * @return widthPixels
     */
    public static Integer getWidthPixels(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    /**
     * 取得頁面高
     *
     * @param context
     * @return heightPixels
     */
    public static Integer getHeightPixels(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    /**
     * 取得在此裝置中，DP換成Pixel為多少
     *
     * @param context
     * @param dp
     * @return pixels
     */
    public static int getDpToPx(Context context, int dp) {
        return (int) (dp * getDpFactor(context));
    }

    /**
     * 取得在此裝置中，DP換成Pixel的倍率
     *
     * @param context
     * @return pixels
     */
    public static float getDpFactor(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }


    public static String getDeviceDetail() {
        StringBuilder dtl = new StringBuilder(1300);
        dtl.append("{");
        dtl.append("\"BUILD\": {\"BOARD\":\"").append(Build.BOARD).append("\"")
                .append("\n").append(",\"BOOTLOADER\":\"").append(Build.BOOTLOADER).append("\"")
                .append("\n").append(",\"BRAND\":\"").append(Build.BRAND).append("\"")
                .append("\n").append(",\"DEVICE\":\"").append(Build.DEVICE).append("\"")
                .append("\n").append(",\"DISPLAY\":\"").append(Build.DISPLAY).append("\"")
                .append("\n").append(",\"FINGERPRINT\":\"").append(Build.FINGERPRINT).append("\"")
                .append("\n").append(",\"HARDWARE\":\"").append(Build.HARDWARE).append("\"")
                .append("\n").append(",\"HOST\":\"").append(Build.HOST).append("\"")
                .append("\n").append(",\"ID\":\"").append(Build.ID).append("\"")
                .append("\n").append(",\"MANUFACTURER\":\"").append(Build.MANUFACTURER).append("\"")
                .append("\n").append(",\"MODEL\":\"").append(Build.MODEL).append("\"")
                .append("\n").append(",\"PRODUCT\":\"").append(Build.PRODUCT).append("\"")
                .append("\n").append(",\"SERIAL\":\"").append(Build.SERIAL).append("\"")
                .append("\n").append(",\"TAGS\":\"").append(Build.TAGS).append("\"")
                .append("\n").append(",\"TIME\":\"").append(Build.TIME).append("\"")
                .append("\n").append(",\"TYPE\":\"").append(Build.TYPE).append("\"")
                .append("\n").append(",\"USER\":\"").append(Build.USER).append("\"")
                //.append("\n").append(",\"VERSION\":{\"BASE_OS\":\"").append(Build.VERSION.BASE_OS).append("\"")
                .append("\n").append(",\"CODENAME\":\"").append(Build.VERSION.CODENAME).append("\"")
                .append("\n").append(",\"INCREMENTAL\":\"").append(Build.VERSION.INCREMENTAL).append("\"")
                //.append("\n").append(",\"PREVIEW_SDK_INT\":\"").append(Build.VERSION.PREVIEW_SDK_INT).append("\"")
                .append("\n").append(",\"RELEASE\":\"").append(Build.VERSION.RELEASE).append("\"")
                .append("\n").append(",\"SDK_INT\":\"").append(Build.VERSION.SDK_INT).append("\"")
                //.append("\n").append(",\"SECURITY_PATCH\":\"").append(Build.VERSION.SECURITY_PATCH).append("\"")
                .append("}")
                .append("}")
                //.append("\n").append(",\"SECURE\":{\"ACCESSIBILITY_DISPLAY_INVERSION_ENABLED\":\"").append(Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED).append("\"")
                .append("\n").append(",\"ACCESSIBILITY_ENABLED\":\"").append(Settings.Secure.ACCESSIBILITY_ENABLED).append("\"")
                .append("\n").append(",\"ACCESSIBILITY_SPEAK_PASSWORD\":\"").append(Settings.Secure.ACCESSIBILITY_SPEAK_PASSWORD).append("\"")
                .append("\n").append(",\"ALLOWED_GEOLOCATION_ORIGINS\":\"").append(Settings.Secure.ALLOWED_GEOLOCATION_ORIGINS).append("\"")
                .append("\n").append(",\"ANDROID_ID\":\"").append(Settings.Secure.ANDROID_ID).append("\"")
                .append("\n").append(",\"CONTENT_URI\":\"").append(Settings.Secure.CONTENT_URI).append("\"")
                .append("\n").append(",\"DEFAULT_INPUT_METHOD\":\"").append(Settings.Secure.DEFAULT_INPUT_METHOD).append("\"")
                .append("\n").append(",\"ENABLED_ACCESSIBILITY_SERVICES\":\"").append(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).append("\"")
                .append("\n").append(",\"ENABLED_INPUT_METHODS\":\"").append(Settings.Secure.ENABLED_INPUT_METHODS).append("\"")
                .append("\n").append(",\"INPUT_METHOD_SELECTOR_VISIBILITY\":\"").append(Settings.Secure.INPUT_METHOD_SELECTOR_VISIBILITY).append("\"")
                .append("\n").append(",\"INSTALL_NON_MARKET_APPS\":\"").append(Settings.Secure.INSTALL_NON_MARKET_APPS).append("\"")
                .append("\n").append(",\"LOCATION_MODE\":\"").append(Settings.Secure.LOCATION_MODE).append("\"")
//				.append("\n").append(",\"LOCATION_MODE_BATTERY_SAVING\":\"").append(Secure.LOCATION_MODE_BATTERY_SAVING).append("\"")
//				.append("\n").append(",\"LOCATION_MODE_HIGH_ACCURACY\":\"").append(Secure.LOCATION_MODE_HIGH_ACCURACY).append("\"")
//				.append("\n").append(",\"LOCATION_MODE_OFF\":\"").append(Secure.LOCATION_MODE_OFF).append("\"")
//				.append("\n").append(",\"LOCATION_MODE_SENSORS_ONLY\":\"").append(Secure.LOCATION_MODE_SENSORS_ONLY).append("\"")
                .append("\n").append(",\"PARENTAL_CONTROL_ENABLED\":\"").append(Settings.Secure.PARENTAL_CONTROL_ENABLED).append("\"")
                .append("\n").append(",\"PARENTAL_CONTROL_LAST_UPDATE\":\"").append(Settings.Secure.PARENTAL_CONTROL_LAST_UPDATE).append("\"")
                .append("\n").append(",\"PARENTAL_CONTROL_REDIRECT_URL\":\"").append(Settings.Secure.PARENTAL_CONTROL_REDIRECT_URL).append("\"")
                .append("\n").append(",\"SELECTED_INPUT_METHOD_SUBTYPE\":\"").append(Settings.Secure.SELECTED_INPUT_METHOD_SUBTYPE).append("\"")
                .append("\n").append(",\"SETTINGS_CLASSNAME\":\"").append(Settings.Secure.SETTINGS_CLASSNAME).append("\"")
                //.append("\n").append(",\"SKIP_FIRST_USE_HINTS\":\"").append(Secure.SKIP_FIRST_USE_HINTS).append("\"")
                .append("\n").append(",\"TOUCH_EXPLORATION_ENABLED\":\"").append(Settings.Secure.TOUCH_EXPLORATION_ENABLED).append("\"")
                .append("\n").append(",\"TTS_DEFAULT_PITCH\":\"").append(Settings.Secure.TTS_DEFAULT_PITCH).append("\"")
                .append("\n").append(",\"TTS_DEFAULT_RATE\":\"").append(Settings.Secure.TTS_DEFAULT_RATE).append("\"")
                .append("\n").append(",\"TTS_DEFAULT_SYNTH\":\"").append(Settings.Secure.TTS_DEFAULT_SYNTH).append("\"")
                .append("\n").append(",\"TTS_ENABLED_PLUGINS\":\"").append(Settings.Secure.TTS_ENABLED_PLUGINS).append("\"")
                .append("}")
                .append("}");
        return dtl.toString();
    }
}
