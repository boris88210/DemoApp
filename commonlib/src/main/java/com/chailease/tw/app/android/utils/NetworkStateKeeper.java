package com.chailease.tw.app.android.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import java.util.HashMap;

/**
 * <receiver android:name="com.chailease.tw.app.android.utils.NetworkStateKeeper" >
 * <intent-filter>
 * <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
 * <action android:name="android.net.wifi.WIFI_STATE_CHANGED" />
 * </intent-filter>
 * </receiver>
 */
public class NetworkStateKeeper extends BroadcastReceiver {

    static boolean network = false;
    static HashMap<String, NetworkStateChangeListener> listeners = new HashMap<String, NetworkStateChangeListener>();

    public static void registerListener(Activity activity) {
        if (activity instanceof NetworkStateChangeListener) {
            listeners.put(activity.getClass().getSimpleName(), (NetworkStateChangeListener) activity);
        } else {
            LogUtility.debug(new NetworkStateKeeper(),
                    "registerListener",
                    activity.getClass().getName() + " is not a NetworkStateChangeListener");
        }
    }

    public static void unregisterListener(Activity activity) {
        if (activity instanceof NetworkStateChangeListener) {
            if (listeners.containsKey(activity.getClass().getSimpleName())) {
                listeners.remove(activity.getClass().getSimpleName());
            }
        } else {
            LogUtility.debug(new NetworkStateKeeper(),
                    "unregisterListener",
                    activity.getClass().getName() + " is not a NetworkStateChangeListener");
        }
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

//		NetworkInfo mNetworkInfo = (NetworkInfo) intent
//				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_TYPE);
//		if (!intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)
//				|| intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
//				|| mNetworkInfo == null) {
//			return;
//		}
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission") NetworkInfo mNetworkInfo = connMgr.getActiveNetworkInfo();

        // When Network State Changed than do notify listener
        boolean lastState = network;
        network = checkoutNetwork(context, mNetworkInfo);
        if (listeners.size() > 0 && lastState != network) {
            NetworkStateChangeListener[] ls = listeners.values().toArray(new NetworkStateChangeListener[listeners.size()]);
            if (network) {
                for (NetworkStateChangeListener listener : ls)
                    listener.notifyAvailable();
            } else {
                for (NetworkStateChangeListener listener : ls)
                    listener.notifyUnavailable();
            }
        }
    }

    private boolean checkoutNetwork(Context context, NetworkInfo networInfo) {
        /* 判斷是否有網路 */
        if (networInfo == null || networInfo.getState() != State.CONNECTED) {
            /* 無網路 */
            return false;
        } else {
            /* 有網路 */
            return true;
        }
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (LogUtility.isCompilerLog)
            LogUtility.d(getClass().getName(), "isOnline", "ready to check network state");
        network = netInfo != null && netInfo.isConnected();
        if (LogUtility.isCompilerLog)
            LogUtility.d(getClass().getName(), "isOnline", "network state:" + network);
        return network;
    }

    /**
     * one of TYPE_MOBILE, TYPE_WIFI, TYPE_WIMAX, TYPE_ETHERNET, TYPE_BLUETOOTH, or other types defined by ConnectivityManager
     *
     * @param context
     * @return
     */
    public int checkNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo.getType();
    }

    public boolean isOnline() {
        return network;
    }

    public interface NetworkStateChangeListener {
        void notifyAvailable();

        void notifyUnavailable();
    }

}