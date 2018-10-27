package com.development.borissu.demoapp.database.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;


//使用Converter將DB無法儲存的類別轉為基本型別，或將db的資料轉為想要的類別
public class Converter {
    @TypeConverter
    public static Date timestampToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long DateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
