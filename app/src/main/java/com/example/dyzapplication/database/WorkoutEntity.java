package com.example.dyzapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workouts")
public class WorkoutEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String date;
    private String bodyPart;
    private int intensity;
    private int duration;
    private String exercises;
    private String customExercises;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getBodyPart() { return bodyPart; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }
    public int getIntensity() { return intensity; }
    public void setIntensity(int intensity) { this.intensity = intensity; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getExercises() { return exercises; }
    public void setExercises(String exercises) { this.exercises = exercises; }
    public String getCustomExercises() { return customExercises; }
    public void setCustomExercises(String customExercises) { this.customExercises = customExercises; }
} 