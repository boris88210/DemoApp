package com.chailease.tw.app.android.update.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

import com.chailease.tw.app.android.data.api.LoginData;

/**
 * Created by 10470 on 2018/6/11.
 */

public class LoginDataParcelable implements Parcelable {

    public String MEMBER_ID;
    public String CUSTNAME;
    public String APPROVE_POLICY;
    public String VERIFY_CODE;
    public String IS_NEWAPP;
    public String APP_URL;
    public String USR_EMAIL;
    public String DEPT_LVL;
    public String DEPT_ID;
    public String DEPT_CD;
    public String DEPT_NME;
    public String DEPT_HD_USR_ID;
    public String DEPT_COMP_ID;
    public String COMP_SHRT_NME;
    public String MVPN;
//        public com.chailease.tw.app.android.data.api.LoginData.USR_PRFL[] USR_PRFL;
//        public JsonElement LOGIN_OUTPUT_JSON;

    public LoginDataParcelable() {
    }

    public LoginDataParcelable(LoginData.LOGIN_DATA loginData) {
        this.MEMBER_ID = loginData.MEMBER_ID;
        this.CUSTNAME = loginData.CUSTNAME;
        this.APPROVE_POLICY = loginData.APPROVE_POLICY;
        this.VERIFY_CODE = loginData.VERIFY_CODE;
        this.IS_NEWAPP = loginData.IS_NEWAPP;
        this.APP_URL = loginData.APP_URL;
        this.USR_EMAIL = loginData.USR_EMAIL;
        this.DEPT_LVL = loginData.DEPT_LVL;
        this.DEPT_ID = loginData.DEPT_ID;
        this.DEPT_CD = loginData.DEPT_CD;
        this.DEPT_NME = loginData.DEPT_NME;
        this.DEPT_HD_USR_ID = loginData.DEPT_HD_USR_ID;
        this.DEPT_COMP_ID = loginData.DEPT_COMP_ID;
        this.COMP_SHRT_NME = loginData.COMP_SHRT_NME;
        this.MVPN = loginData.MVPN;
//            this.USR_PRFL = loginData.USR_PRFL;
//            this.LOGIN_OUTPUT_JSON = loginData.LOGIN_OUTPUT_JSON;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MEMBER_ID);
        dest.writeString(CUSTNAME);
        dest.writeString(APPROVE_POLICY);
        dest.writeString(VERIFY_CODE);
        dest.writeString(IS_NEWAPP);
        dest.writeString(APP_URL);
        dest.writeString(USR_EMAIL);
        dest.writeString(DEPT_LVL);
        dest.writeString(DEPT_ID);
        dest.writeString(DEPT_CD);
        dest.writeString(DEPT_NME);
        dest.writeString(DEPT_HD_USR_ID);
        dest.writeString(DEPT_COMP_ID);
        dest.writeString(COMP_SHRT_NME);
        dest.writeString(MVPN);
    }

    public static final Parcelable.Creator<LoginDataParcelable> CREATOR = new Creator<LoginDataParcelable>() {


        @Override
        public LoginDataParcelable createFromParcel(Parcel source) {
            LoginDataParcelable data = new LoginDataParcelable();

            data.MEMBER_ID = source.readString();
            data.CUSTNAME = source.readString();
            data.APPROVE_POLICY = source.readString();
            data.VERIFY_CODE = source.readString();
            data.IS_NEWAPP = source.readString();
            data.APP_URL = source.readString();
            data.USR_EMAIL = source.readString();
            data.DEPT_LVL = source.readString();
            data.DEPT_ID = source.readString();
            data.DEPT_CD = source.readString();
            data.DEPT_NME = source.readString();
            data.DEPT_HD_USR_ID = source.readString();
            data.DEPT_COMP_ID = source.readString();
            data.COMP_SHRT_NME = source.readString();
            data.MVPN = source.readString();
//                public com.chailease.tw.app.android.data.api.LoginData.USR_PRFL[] USR_PRFL;
//                public JsonElement LOGIN_OUTPUT_JSON;
//
            return data;
        }

        @Override
        public LoginDataParcelable[] newArray(int size) {
            return new LoginDataParcelable[size];
        }


    };
}
