package com.example.dyzapplication.database;

import androidx.room.*;
import java.util.List;

@Dao
public interface WorkoutDao {
    @Insert
    long insert(WorkoutEntity workout);

    @Update
    void update(WorkoutEntity workout);

    @Delete
    void delete(WorkoutEntity workout);

    @Query("SELECT * FROM workouts WHERE date = :date")
    WorkoutEntity getWorkoutByDate(String date);

    @Query("SELECT * FROM workouts ORDER BY date DESC")
    List<WorkoutEntity> getAllWorkouts();

    @Query("SELECT * FROM workouts WHERE strftime('%Y-%m', date) = :yearMonth")
    List<WorkoutEntity> getWorkoutsByMonth(String yearMonth);
} 