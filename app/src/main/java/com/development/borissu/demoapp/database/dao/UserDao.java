package com.development.borissu.demoapp.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.development.borissu.demoapp.database.entity.User;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {


    @Insert(onConflict = REPLACE)//如果重複的時候就取代
    public void inserUsers(User... users);

    @Update
    public int updateUsers(User... users);//回傳被更動的資料數量

    @Query("SELECT * FROM User")
    public List<User> getAllUsers();

    @Query("SELECT * FROM user WHERE User_Id = :userId")
    public User getUser(String userId);


    @Delete
    public int deleteUsers(User... users);//回傳刪除的資料數量

}
