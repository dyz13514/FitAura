package com.example.dyzapplication.database;

import androidx.room.*;
import java.util.List;

@Dao
public interface PlanDao {
    @Insert
    long insert(PlanEntity plan);

    @Update
    void update(PlanEntity plan);

    @Delete
    void delete(PlanEntity plan);

    @Query("SELECT * FROM plans WHERE userName = :userName ORDER BY date DESC, time DESC")
    List<PlanEntity> getAllPlans(String userName);

    @Query("SELECT * FROM plans WHERE userName = :userName AND date = :date")
    List<PlanEntity> getPlansByDate(String userName, String date);

    @Query("SELECT * FROM plans WHERE id = :id")
    PlanEntity getPlanById(long id);
} 