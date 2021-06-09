package com.example.roompractice;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.PrimaryKey;
import androidx.room.Room;

import java.util.List;

// AndroidViewModel -> 생성자에 application의 Context가 넘어오기 때문에 ViewModel 내에서 Context를 쓸 일이 있을 때 사용.
public class UserProfileViewModel extends AndroidViewModel {
    public LiveData<List<UserProfile>> userProfileList;
    public UserProfileDao userProfileDao;

    public UserProfileViewModel(@NonNull @org.jetbrains.annotations.NotNull Application application) {
        super(application);

        UserProfileDatabase db = Room.databaseBuilder(application, UserProfileDatabase.class, "userprofile").build();
        userProfileDao = db.getUserProfileDao();
        userProfileList = db.getUserProfileDao().getAll();
    }

    public void insert(UserProfile userProfile) {
        new InsertUserProfileAsyncTask().execute(userProfile);
    }

    public void delete(UserProfile userProfile) {
        new DeleteUserProfileAsyncTask().execute(userProfile);
    }

    //별도의 쓰레드 생성. AsyncTask가 제일 간편.
    private class InsertUserProfileAsyncTask extends AsyncTask<UserProfile, Void, Void> {
        @Override
        protected Void doInBackground(UserProfile... userProfiles) {
            userProfileDao.insert(userProfiles[0]);
            return null;
        }
    }

    private class DeleteUserProfileAsyncTask extends AsyncTask<UserProfile, Void, Void> {
        @Override
        protected Void doInBackground(UserProfile... userProfiles) {
            userProfileDao.delete(userProfiles[0]);
            return null;
        }
    }
}
