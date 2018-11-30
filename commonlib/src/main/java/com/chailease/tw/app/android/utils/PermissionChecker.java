package com.chailease.tw.app.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.HashSet;
import java.util.LinkedList;

/**
 *  To call checkPermission first to verify the permissions,
 *  if some permission defined that mean may be needed to request permission for APP;
 *  call grantPermissions after checkPermission with defined permissions,
 *  if get returns from grantPermissions that mean some permission need an explanation for requested permission to user,
 *  after explanation then call grantExplanationPermissions to request permission.
 *  the passing Activity must be current UI activity and support OnRequestPermissionsResultCallback
 *
 *  remember: Beginning in Android 6.0 (API level 23), users grant permissions to apps while the app is running, not when they install the app.
 *   It also gives the user more control over the app's functionality; for example,
 *   a user could choose to give a camera app access to the camera but not to the device location.
 *   The user can revoke the permissions at any time, by going to the app's Settings screen.
 *   the following permission needs to check before activate service:
 *      https://developer.android.com/guide/topics/permissions/normal-permissions.html
 *      https://developer.android.com/guide/topics/permissions/requesting.html#normal-dangerous
 *      update at : 2017/4/10
 *      Dangerous Permission
 *          Permission Group                            Permissions
 *          CALENDAR                                 • READ_CALENDAR
 *                                                                • WRITE_CALENDAR
 *          CAMERA                                      • CAMERA
 *          CONTACTS                                  • READ_CONTACTS
 *                                                                • WRITE_CONTACTS
 *                                                                • GET_ACCOUNTS
 *          LOCATION                                   • ACCESS_FINE_LOCATION
 *                                                                • ACCESS_COARSE_LOCATION
 *          MICROPHONE                             • RECORD_AUDIO
 *          PHONE                                          • READ_PHONE_STATE
 *                                                                • CALL_PHONE
 *                                                                • READ_CALL_LOG
 *                                                                • WRITE_CALL_LOG
 *                                                                • ADD_VOICEMAIL
 *                                                                • USE_SIP
 *                                                                • PROCESS_OUTGOING_CALLS
 *         SENSORS                                       • BODY_SENSORS
 *         SMS                                                • SEND_SMS
 *                                                                • RECEIVE_SMS
 *                                                                • READ_SMS
 *                                                                • RECEIVE_WAP_PUSH
 *                                                                • RECEIVE_MMS
 *         STORAGE                                      • READ_EXTERNAL_STORAGE
 *                                                                 • WRITE_EXTERNAL_STORAGE
 */
public class PermissionChecker {

	public static final HashSet<String> DANGEROUS_PERMISSIONS = new HashSet<String>();
	static {
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.CALENDAR);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.READ_CALENDAR);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.WRITE_CALENDAR);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.CAMERA);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.CAMERA);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.CONTACTS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.READ_CONTACTS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.WRITE_CONTACTS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.GET_ACCOUNTS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.LOCATION);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.MICROPHONE);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.RECORD_AUDIO);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.PHONE);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.READ_PHONE_STATE);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.CALL_PHONE);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.READ_CALL_LOG);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.WRITE_CALL_LOG);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.ADD_VOICEMAIL);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.USE_SIP);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.PROCESS_OUTGOING_CALLS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.SENSORS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.BODY_SENSORS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.SMS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.SEND_SMS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.RECEIVE_SMS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.READ_SMS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.RECEIVE_WAP_PUSH);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.RECEIVE_MMS);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission_group.STORAGE);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
		DANGEROUS_PERMISSIONS.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}

	public static boolean[] checkPermissions(Context context, String... permissions) {
		boolean[] result = new boolean[permissions.length];
		for (int i=0; i<result.length; i++) {
			if(PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(context, permissions[i]))
				result[i] = true;
			else result[i] = false;
		}
		return result;
	}

	/**
	 * 如果權限為取得可透過此 API 對使用者進行權限請求，一般請況可透過系統預設介面進行權限請求設定，故需提供 OnRequestPermissionsResultCallback 以處理請求結果；
	 * 如果部分權限須提供原因說明，則會回傳需原因說明的 權限清單
	 * @param activity
	 * @param requestCode 搭配 activity OnRequestPermissionsResultCallback 的 request-code
	 * @param permissions
	 * @return  如果有權限請求需進行理由說明者
	 */
	public static String[] grantPermissions(Activity activity, int requestCode, String... permissions) {
		boolean needToExplanation = false;

		LinkedList<String> explanationPermissions = new LinkedList<String>();
		LinkedList<String> defaultSystemDialogPermissions = new LinkedList<String>();
		for (String permission : permissions) {
			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
				needToExplanation = true;
				explanationPermissions.add(permission);
			} else {
				defaultSystemDialogPermissions.add(permission);
			}
		}
		if (defaultSystemDialogPermissions.size()>0 && activity instanceof ActivityCompat.OnRequestPermissionsResultCallback) {
			// No explanation needed, we can request the permission.
			ActivityCompat.requestPermissions(activity,
					defaultSystemDialogPermissions.toArray(new String[defaultSystemDialogPermissions.size()]),
					requestCode);
			// requestCode is an app-defined int constant.
			// The callback method gets the result of the request.
		}
		return explanationPermissions.size()>0 ? explanationPermissions.toArray(new String[explanationPermissions.size()]) : null;
	}

	/**
	 *  先透過 #grantPermissions 取得須補充說明原因的設定項； 在完成提供設定說明後可直接透過此方法向使用者請求權限
	 * @param activity  當前的 Activity 必須具備處理權限請求後的處理；比方說取得權限後重新發動處理程序
	 * @param requestCode   搭配 activity OnRequestPermissionsResultCallback 的 request-code
	 * @param permissions
	 * @return
	 */
	public static boolean grantExplanationPermissions(Activity activity, int requestCode, String... permissions) {
		if (activity instanceof ActivityCompat.OnRequestPermissionsResultCallback) {
			// after Explanation , we can request the permission.
			ActivityCompat.requestPermissions(activity,
					permissions,
					requestCode);
			// requestCode is an app-defined int constant.
			// The callback method gets the result of the request.
		}
		return true;
	}

	/**
	 * Grant 成功後欲執行的工作實作在 grant 中
	 * Grant 失敗後欲執行的工作實作在 afterDeny 中
	 * mark 註記用內容, 回傳空值表示不給追蹤
	 * execute 在請求完權限後的執行器, 可用於 onRequestPermissionsResult
	 */
	abstract public static class GrantExecutor {
		public boolean grant;
		abstract public void grant();
		abstract public void afterDeny();
		public String mark() {
			return getClass().getSimpleName();
		}
		final public void execute() {
			if (grant) grant();
			else afterDeny();
		}
	}
}