package com.development.borissu.demoapp.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.development.borissu.demoapp.database.converter.Converter;

import java.util.Date;

@Entity(tableName = "user")
public class User {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private String uid;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    @ColumnInfo(name = "create_date")
    @TypeConverters(Converter.class)
    private Date createDate;


    //---Constructor---

    public User() {

    }

    public User(String id) {
        this();
        this.uid = id;
    }

    public User(String id, String firstName, String lastName) {
        this();
        this.uid = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    //---Getter---

    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName == null ? "" : firstName;
    }

    public String getLastName() {
        return lastName == null ? "" : lastName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    //---Setter---

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
