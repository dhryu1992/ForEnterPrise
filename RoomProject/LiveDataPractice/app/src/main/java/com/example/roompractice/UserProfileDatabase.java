package com.example.roompractice;

import androidx.room.Database;
import androidx.room.RoomDatabase;

//데이터베이스에서는 여러개의 entity를 관리할 수 있음. Array로 전달가능. 여기선 하나의 entity만 사용 {안에 , 로 여러개 사용가능}

@Database(entities = {UserProfile.class}, version = 1)
public abstract class UserProfileDatabase extends RoomDatabase {
    // 데이터베이스 객체를 만든 후 UserProfile 데이터를 입력, 수정, 삭제, 조회할 수 있는 Dao 객체를 생성해야함.
    public abstract UserProfileDao getUserProfileDao();
}
