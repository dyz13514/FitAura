package com.example.dyzapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plans")
public class PlanEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String content;
    private String date;
    private String time;
    private String state;
    private String userName;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
} 