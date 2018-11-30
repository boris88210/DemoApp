package com.chailease.tw.app.android.constants;

/**
 *
 */
public class SystemConstants {

	public static final String LOG_TAG_COMMONLIB    =   "CLH.CommonLib";
	public static final String LOG_TAG_INIT =   "APP_INIT";

	public static final String APP_TYPE_ANDROID =   "1";

	public static final String APP_META_DATA_NAME_APP_ID =   "APP_ID";
	public static final String APP_META_DATA_NAME_API_CLIENT_FACTORY    =   "API_CLIENT_FACTORY_IMPL";
	public static final String APP_META_DATA_NAME_PREF_CRYPT_KEY         =   "PREF_KEY";

	public static final String PUSH_KEY_EMPTY   =   "-";
	public static final String DEMO_SECU_KEY = "_@*#66de_";

	public static final int WORK_TYPE_SALES          =   1;
	public static final int WORK_TYPE_CRS            =   2;
	public static final int WORK_TYPE_SERVICE       =   3;
	public static final int WORK_TYPE_DUN            =   4;
	public static final int WORK_TYPE_STAFF          =   5;

	// Get max notifyAvailable VM memory, exceeding this amount will throw an
	// OutOfMemory exception.
	public static final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);

	// 1, 成功, 2登入失敗 3.驗證碼失敗 4.其他失敗
	public static final int API_STATUS_SUCCESS = 0;
	public static final int API_STATUS_LOGON_FAIL = 2;
	public static final int API_STATUS_NONAUTH = 3;
	public static final int API_STATUS_OTHER_CAUSE = 4;

	public static final int STATUS_Y = 1;
	public static final int STATUS_N = 2;
	/**
	 *  Service 的每天幾點清除
	 */
	public static final int CLEAN_OVERDUE_IMG_IN_HOUR = 7;
	/**
	 *  案件圖片有效期(天)
	 */
	public static final int CLEAN_OVERDUE_IMG_KEEPDAYS = 14;
	/**
	 * 檔案剩餘空間 在 200M 以下，案件圖片有效期(天)
	 */
	public static final int CLEAN_OVERDUE_IMG_KEEPDAYS_LESS_SUGGEST = 7;
	/**
	 * 檔案剩餘空間，低於200M， 200M = 200* 1024* 1024 = 209715200
	 */
	public static final int INERNAL_MEMORY_SIZE_SUGGEST = 209715200;
	/**
	 * 檔案剩餘空間，最低限制 50M = 50* 1024* 1024 = 52428800
	 */
	public static final int INERNAL_MEMORY_SIZE_LIMIT = 52428800;
	public static final int UPLOAD_OPTIONS_GOOD_IMAGE_WIDTH = 2048;
	public static final int UPLOAD_OPTIONS_GOOD_IMAGE_HEIGHT = 1536;
	public static final int UPLOAD_OPTIONS_IMAGE_QUALITY = 50;
	/**
	 * 產生亂數字串使用的基本字元
	 */
	public static final String BASE_RANDOM_STRING = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final int AES_KEY_LENGTH = 32;
	public static final int AES_VI_LENGTH = 16;
	// 呼叫API超時設定，上傳檔案不適用
	public static final int NETWORK_TIMEOUT = 30000;
	// 處理中超過幾秒可以取消
	public static final int DIALOG_CAN_DISMISS_SEC = 30;

}