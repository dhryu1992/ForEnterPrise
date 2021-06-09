package com.example.roompractice;

//Dao는 데이터베이스에서 데이터를 엑세스 하기위한 객체.

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Room에서 Dao 객체를 만들어주기위해 @Dao를 붙힌다
@Dao
public interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserProfile userProfile);

    @Update
    void update(UserProfile userProfile);

    @Delete
    void delete(UserProfile userProfile);

    @Query("SELECT * FROM UserProfile")
    LiveData<List<UserProfile>> getAll();

    @Query("DELETE FROM UserProfile")
    void deleteAll();
}
