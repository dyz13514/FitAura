package com.example.dyzapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 健身计划管理器，负责管理健身任务的轮换和难度
 */
public class WorkoutPlanManager {
    private static final String TAG = "WorkoutPlanManager";
    private static final String PREFS_NAME = "WorkoutPrefs";
    private static final String LAST_WORKOUT_DATE = "lastWorkoutDate";
    private static final String LAST_BODY_PART = "lastBodyPart";
    private static final String LAST_INTENSITY = "lastIntensity";
    
    // 身体部位和对应的顺序
    private static final String[] BODY_PARTS = {"shoulders", "back", "chest", "legs"};
    
    // 强度等级（1-5，从轻到重）
    public static final int INTENSITY_VERY_LIGHT = 1;
    public static final int INTENSITY_LIGHT = 2;
    public static final int INTENSITY_MODERATE = 3;
    public static final int INTENSITY_HARD = 4;
    public static final int INTENSITY_VERY_HARD = 5;
    
    private Context context;
    
    public WorkoutPlanManager(Context context) {
        this.context = context;
    }
    
    /**
     * 获取今天应该训练的身体部位
     */
    public String getTodayBodyPart() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastWorkoutDate = prefs.getString(LAST_WORKOUT_DATE, "");
        String lastBodyPart = prefs.getString(LAST_BODY_PART, "");
        
        String today = getTodayDate();
        
        // 如果今天已经训练过，返回上次的部位
        if (today.equals(lastWorkoutDate) && !lastBodyPart.isEmpty()) {
            return lastBodyPart;
        }
        
        // 否则确定下一个训练部位
        int nextIndex = 0;
        if (!lastBodyPart.isEmpty()) {
            for (int i = 0; i < BODY_PARTS.length; i++) {
                if (BODY_PARTS[i].equals(lastBodyPart)) {
                    nextIndex = (i + 1) % BODY_PARTS.length;
                    break;
                }
            }
        }
        
        return BODY_PARTS[nextIndex];
    }
    
    /**
     * 记录完成的训练
     */
    public void recordWorkout(String bodyPart, int intensity) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        editor.putString(LAST_WORKOUT_DATE, getTodayDate());
        editor.putString(LAST_BODY_PART, bodyPart);
        editor.putInt(LAST_INTENSITY, intensity);
        
        editor.apply();
    }
    
    /**
     * 获取推荐的训练强度
     */
    public int getRecommendedIntensity() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int lastIntensity = prefs.getInt(LAST_INTENSITY, INTENSITY_MODERATE);
        
        // 根据上次的强度调整今天的推荐强度
        if (lastIntensity >= INTENSITY_HARD) {
            // 如果上次强度较高，今天适当减轻
            return Math.max(INTENSITY_LIGHT, lastIntensity - 1);
        } else if (lastIntensity <= INTENSITY_LIGHT) {
            // 如果上次强度较低，今天适当增加
            return Math.min(INTENSITY_HARD, lastIntensity + 1);
        } else {
            // 中等强度保持不变
            return lastIntensity;
        }
    }
    
    /**
     * 检查今天是否已经完成训练
     */
    public boolean isTodayWorkoutCompleted() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastWorkoutDate = prefs.getString(LAST_WORKOUT_DATE, "");
        return getTodayDate().equals(lastWorkoutDate);
    }
    
    /**
     * 获取基于强度的训练动作和组数
     */
    public Map<String, Integer> getExercisesWithSets(String bodyPart, int intensity) {
        Map<String, Integer> exercisesWithSets = new HashMap<>();
        String[] exercises = getExercisesForBodyPart(bodyPart);
        
        // 根据强度确定组数
        int baseSets = 3; // 中等强度的基础组数
        int sets = baseSets;
        
        if (intensity == INTENSITY_VERY_LIGHT) {
            sets = 2;
        } else if (intensity == INTENSITY_LIGHT) {
            sets = 3;
        } else if (intensity == INTENSITY_MODERATE) {
            sets = 4;
        } else if (intensity == INTENSITY_HARD) {
            sets = 5;
        } else if (intensity == INTENSITY_VERY_HARD) {
            sets = 6;
        }
        
        // 为每个动作分配组数
        for (String exercise : exercises) {
            exercisesWithSets.put(exercise, sets);
        }
        
        return exercisesWithSets;
    }
    
    /**
     * 获取强度描述
     */
    public String getIntensityDescription(int intensity) {
        switch (intensity) {
            case INTENSITY_VERY_LIGHT:
                return "非常轻松";
            case INTENSITY_LIGHT:
                return "轻松";
            case INTENSITY_MODERATE:
                return "适中";
            case INTENSITY_HARD:
                return "困难";
            case INTENSITY_VERY_HARD:
                return "非常困难";
            default:
                return "适中";
        }
    }
    
    /**
     * 获取今天的日期字符串
     */
    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // 月份从0开始
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return year + "-" + month + "-" + day;
    }
    
    /**
     * 获取不同部位的训练动作
     */
    private String[] getExercisesForBodyPart(String bodyPart) {
        switch (bodyPart) {
            case "shoulders":
                return new String[]{"哑铃推举", "侧平举", "前平举", "反向飞鸟"};
            case "back":
                return new String[]{"引体向上", "杠铃划船", "高位下拉", "坐姿划船"};
            case "chest":
                return new String[]{"平板卧推", "上斜卧推", "哑铃飞鸟", "双杠臂屈伸"};
            case "legs":
                return new String[]{"深蹲", "硬拉", "腿举", "弓步蹲"};
            default:
                return new String[]{};
        }
    }
    
    /**
     * 获取身体部位的中文名称
     */
    public String getBodyPartChineseName(String bodyPart) {
        switch (bodyPart) {
            case "shoulders":
                return "肩部";
            case "back":
                return "背部";
            case "chest":
                return "胸部";
            case "legs":
                return "腿部";
            default:
                return "";
        }
    }
} 