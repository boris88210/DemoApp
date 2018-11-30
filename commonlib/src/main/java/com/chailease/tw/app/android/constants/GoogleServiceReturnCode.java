package com.chailease.tw.app.android.constants;


import com.chailease.tw.app.android.commonlib.R;
import com.google.android.gms.common.ConnectionResult;

/**
 *
 */
public class GoogleServiceReturnCode {
    static public final int CONNECTION_RESULT_RS_API_UNAVAILABLE = R.string.err_google_service_api_unavailable;
    static public final int CONNECTION_RESULT_RS_CANCELED = R.string.err_google_service_canceled;
    static public final int CONNECTION_RESULT_RS_DEVELOPER_ERROR	= R.string.err_google_service_develop_error;
    //static public final int CONNECTION_RESULT_RS_DRIVE_EXTERNAL_STORAGE_REQUIRED	= R.string.err_google_service_external_storage_required;
    static public final int CONNECTION_RESULT_RS_INTERNAL_ERROR	= R.string.err_google_service_internal_error;
    static public final int CONNECTION_RESULT_RS_INTERRUPTED	= R.string.err_google_service_interrupted;
    static public final int CONNECTION_RESULT_RS_INVALID_ACCOUNT	= R.string.err_google_service_invalid_account;
    static public final int CONNECTION_RESULT_RS_LICENSE_CHECK_FAILED	= R.string.err_google_service_license_failed;
    static public final int CONNECTION_RESULT_RS_NETWORK_ERROR	= R.string.err_google_service_nerwork_error;
    static public final int CONNECTION_RESULT_RS_RESOLUTION_REQUIRED	= R.string.err_google_service_resolution_required;
    static public final int CONNECTION_RESULT_RS_RESTRICTED_PROFILE	= R.string.err_google_service_restricted_profile;
    static public final int CONNECTION_RESULT_RS_SERVICE_DISABLED	= R.string.err_google_service_disabled;
    static public final int CONNECTION_RESULT_RS_SERVICE_INVALID	= R.string.err_google_service_invalid;
    static public final int CONNECTION_RESULT_RS_SERVICE_MISSING	= R.string.err_google_service_missing;
    static public final int CONNECTION_RESULT_RS_SERVICE_MISSING_PERMISSION	= R.string.err_google_service_missing_permission;
    static public final int CONNECTION_RESULT_RS_SERVICE_UPDATING	= R.string.err_google_service_updating;
    static public final int CONNECTION_RESULT_RS_SERVICE_VERSION_UPDATE_REQUIRED	= R.string.err_google_service_version_update_required;
    static public final int CONNECTION_RESULT_RS_SIGN_IN_FAILED	= R.string.err_google_service_sign_in_failed;
    static public final int CONNECTION_RESULT_RS_SIGN_IN_REQUIRED	= R.string.err_google_service_sign_in_required;
    static public final int CONNECTION_RESULT_RS_SUCCESS	= R.string.info_google_service_success;
    static public final int CONNECTION_RESULT_RS_TIMEOUT	= R.string.err_google_service_timeout;

    public static int CONNECTION_RESULR_RS(ConnectionResult cr) {
        switch (cr.getErrorCode()) {
            case ConnectionResult.API_UNAVAILABLE:
                return CONNECTION_RESULT_RS_API_UNAVAILABLE;
            case ConnectionResult.CANCELED:
                return CONNECTION_RESULT_RS_CANCELED;
            case ConnectionResult.DEVELOPER_ERROR:
                return CONNECTION_RESULT_RS_DEVELOPER_ERROR;
//            case ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED:
//                return CONNECTION_RESULT_RS_DRIVE_EXTERNAL_STORAGE_REQUIRED;
            case ConnectionResult.INTERNAL_ERROR:
                return CONNECTION_RESULT_RS_INTERNAL_ERROR;
            case ConnectionResult.INTERRUPTED:
                return CONNECTION_RESULT_RS_INTERRUPTED;
            case ConnectionResult.INVALID_ACCOUNT:
                return CONNECTION_RESULT_RS_INVALID_ACCOUNT;
            case ConnectionResult.LICENSE_CHECK_FAILED:
                return CONNECTION_RESULT_RS_LICENSE_CHECK_FAILED;
            case ConnectionResult.NETWORK_ERROR:
                return CONNECTION_RESULT_RS_NETWORK_ERROR;
            case ConnectionResult.RESOLUTION_REQUIRED:
                return CONNECTION_RESULT_RS_RESOLUTION_REQUIRED;
            case ConnectionResult.RESTRICTED_PROFILE:
                return CONNECTION_RESULT_RS_RESTRICTED_PROFILE;
            case ConnectionResult.SERVICE_DISABLED:
                return CONNECTION_RESULT_RS_SERVICE_DISABLED;
            case ConnectionResult.SERVICE_INVALID:
                return CONNECTION_RESULT_RS_SERVICE_INVALID;
            case ConnectionResult.SERVICE_MISSING:
                return CONNECTION_RESULT_RS_SERVICE_MISSING;
            case ConnectionResult.SERVICE_MISSING_PERMISSION:
                return CONNECTION_RESULT_RS_SERVICE_MISSING_PERMISSION;
            case ConnectionResult.SERVICE_UPDATING:
                return CONNECTION_RESULT_RS_SERVICE_UPDATING;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return CONNECTION_RESULT_RS_SERVICE_VERSION_UPDATE_REQUIRED;
            case ConnectionResult.SIGN_IN_FAILED:
                return CONNECTION_RESULT_RS_SIGN_IN_FAILED;
            case ConnectionResult.SIGN_IN_REQUIRED:
                return CONNECTION_RESULT_RS_SIGN_IN_REQUIRED;
            case ConnectionResult.SUCCESS:
                return CONNECTION_RESULT_RS_SUCCESS;
            case ConnectionResult.TIMEOUT:
                return CONNECTION_RESULT_RS_TIMEOUT;
        }

        return 0;
    }

}
