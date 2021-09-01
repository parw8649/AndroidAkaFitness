package com.example.gymlogpro.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.gymlogpro.GymLog;
import com.example.gymlogpro.User;
import com.example.gymlogpro.db.typeConverters.DateTypeConverter;

@Database(entities = {GymLog.class, User.class}, version = 2)
@TypeConverters(DateTypeConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "GYM_LOG_DATABASE";
    public static final String GYM_LOG_TABLE = "GYM_LOG_TABLE";
    public static final String USER_TABLE = "USER_TABLE";

    public abstract GymLogDao getGymLogDao();
}
