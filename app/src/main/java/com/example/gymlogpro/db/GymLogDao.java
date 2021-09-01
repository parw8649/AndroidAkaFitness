package com.example.gymlogpro.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.gymlogpro.GymLog;
import com.example.gymlogpro.User;

import java.util.List;

@Dao
public interface GymLogDao {

    @Insert
    void insert(GymLog... gymLogs);

    @Update
    void update(GymLog... gymLogs);

    @Delete
    void delete(GymLog gymLogs);

    @Query("SELECT * FROM " + AppDatabase.GYM_LOG_TABLE + " ORDER BY mDate DESC")
    List<GymLog> getAllGymLogs();

    @Query("SELECT * FROM " + AppDatabase.GYM_LOG_TABLE + " WHERE mLogId = :logId")
    List<GymLog> getGymLogsById(int logId);

    @Query("SELECT * FROM " + AppDatabase.GYM_LOG_TABLE + " WHERE mUserId = :mUserId ORDER BY mDate DESC")
    List<GymLog> getGymLogsByUserId(int mUserId);

    @Insert
    void insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE)
    List<User> getAllUsers();

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE mUsername = :mUsername")
    User getUserByUsername(String mUsername);

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE mUserId = :mUserId")
    User getUserByUserId(int mUserId);
}
