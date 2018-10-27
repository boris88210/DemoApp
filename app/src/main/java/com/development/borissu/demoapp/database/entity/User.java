package com.development.borissu.demoapp.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "User")
public class User {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "User_Id")
    private String uid;

    @ColumnInfo(name = "First_Name")
    private String firstName;

    @ColumnInfo(name = "Last_Name")
    private String lastName;


    //---Constructor---
    public User(){

    }

    public User(String id, String firstName, String lastName) {
        this.uid = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }


    //---Getter---

    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
}
