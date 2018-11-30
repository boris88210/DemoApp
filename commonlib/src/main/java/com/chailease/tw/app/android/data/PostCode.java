package com.chailease.tw.app.android.data;

/**
 * 郵遞區號
 *      /assets/postcode.json
 */
public class PostCode {

    public CITY[] CITYS;

    public static class CITY {
        public int CITYID;
        public String CITYNAME;
        public REGION[] REGION;
    }
    public static class REGION {
        public int CITYID;
        public String CITYNAME;
        public int REGIONID;
        public String REGIONNAME;
        public int POSTCODE;
    }

}