package com.development.borissu.demoapp.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.development.borissu.demoapp.database.converter.Converter;
import com.development.borissu.demoapp.database.dao.UserDao;
import com.development.borissu.demoapp.database.entity.User;

//這裡要宣告Database中的table,版本號
@Database(entities = {User.class}, version = 1)
@TypeConverters({Converter.class})//宣告搭配使用的converter
public abstract class MyDataBase extends RoomDatabase {

    //---Constructor---

    //Database需要較多資源，故設計為singleton
    private static MyDataBase INSTANCE;

    public static MyDataBase getINSTANCE(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MyDataBase.class,
                    "User.db")
                    .allowMainThreadQueries()//允許在主執行緒使用
                    .build();
        }
        return INSTANCE;
    }

    //---Dao---

    //Database class中必須包含要使用的Dao
    public abstract UserDao getUserDao();


    //---DB Update---

    //寫入每次DB版本更新的內容，room會依照提供版本的順序執行每個Migration.migrate()
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

        }
    };

}



