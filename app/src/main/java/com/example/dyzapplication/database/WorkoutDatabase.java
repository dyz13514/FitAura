package com.example.dyzapplication.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WorkoutEntity.class, PlanEntity.class}, version = 2, exportSchema = false)
public abstract class WorkoutDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "workout_db";
    private static WorkoutDatabase instance;

    public abstract WorkoutDao workoutDao();
    public abstract PlanDao planDao();

    public static synchronized WorkoutDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                WorkoutDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // 如果数据库版本更新，允许清空数据重建
            .build();
        }
        return instance;
    }
} 