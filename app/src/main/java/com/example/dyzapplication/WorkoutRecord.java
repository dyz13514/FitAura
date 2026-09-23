package com.example.dyzapplication;

import android.content.Context;
import android.util.Log;
import com.example.dyzapplication.database.WorkoutDatabase;
import com.example.dyzapplication.database.WorkoutEntity;
import com.example.dyzapplication.api.ApiService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 训练记录管理器
 */
public class WorkoutRecord {
    private static final String TAG = "WorkoutRecord";
    private final Context context;
    private final WorkoutDatabase database;
    private final ExecutorService executorService;

    public WorkoutRecord(Context context) {
        this.context = context;
        this.database = WorkoutDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void saveWorkout(String date, String bodyPart, int intensity, int duration, 
                          String exercises, String customExercises, Callback callback) {
        executorService.execute(() -> {
            try {
                WorkoutEntity workout = new WorkoutEntity();
                workout.setDate(date);
                workout.setBodyPart(bodyPart);
                workout.setIntensity(intensity);
                workout.setDuration(duration);
                workout.setExercises(exercises);
                workout.setCustomExercises(customExercises);

                // 保存到本地数据库
                long id = database.workoutDao().insert(workout);
                workout.setId(id);

                // 同步到阿里云服务器
                ApiService.uploadWorkout(workout, new ApiService.ApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "云同步成功");
                        if (callback != null) {
                            callback.onSuccess(id);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.w(TAG, "云同步失败，但本地保存成功", e);
                        if (callback != null) {
                            callback.onSuccess(id); // 仍然返回成功，因为本地保存成功
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error saving workout: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    public void getWorkoutByDate(String date, DataCallback<WorkoutData> callback) {
        executorService.execute(() -> {
            try {
                WorkoutEntity entity = database.workoutDao().getWorkoutByDate(date);
                if (entity != null) {
                    WorkoutData data = convertToWorkoutData(entity);
                    callback.onSuccess(data);
                } else {
                    callback.onSuccess(null);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting workout: " + e.getMessage());
                callback.onError(e);
            }
        });
    }

    public void getAllWorkouts(DataCallback<List<WorkoutData>> callback) {
        executorService.execute(() -> {
            try {
                List<WorkoutEntity> entities = database.workoutDao().getAllWorkouts();
                List<WorkoutData> workouts = new ArrayList<>();
                for (WorkoutEntity entity : entities) {
                    workouts.add(convertToWorkoutData(entity));
                }
                callback.onSuccess(workouts);
            } catch (Exception e) {
                Log.e(TAG, "Error getting all workouts: " + e.getMessage());
                callback.onError(e);
            }
        });
    }

    public void getWorkoutDatesInMonth(int year, int month, DataCallback<List<String>> callback) {
        executorService.execute(() -> {
            try {
                String yearMonth = String.format(Locale.US, "%d-%02d", year, month);
                List<WorkoutEntity> workouts = database.workoutDao().getWorkoutsByMonth(yearMonth);
                List<String> dates = new ArrayList<>();
                for (WorkoutEntity workout : workouts) {
                    dates.add(workout.getDate());
                }
                callback.onSuccess(dates);
            } catch (Exception e) {
                Log.e(TAG, "Error getting workout dates: " + e.getMessage());
                callback.onError(e);
            }
        });
    }

    public void deleteWorkout(long id, Callback callback) {
        executorService.execute(() -> {
            try {
                WorkoutEntity workout = new WorkoutEntity();
                workout.setId(id);
                database.workoutDao().delete(workout);
                if (callback != null) {
                    callback.onSuccess(id);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting workout: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    private WorkoutData convertToWorkoutData(WorkoutEntity entity) {
        WorkoutData data = new WorkoutData();
        data.id = entity.getId();
        data.date = entity.getDate();
        data.bodyPart = entity.getBodyPart();
        data.intensity = entity.getIntensity();
        data.duration = entity.getDuration();
        data.exercises = entity.getExercises();
        data.customExercises = entity.getCustomExercises();
        return data;
    }

    public void close() {
        executorService.shutdown();
    }

    public interface Callback {
        void onSuccess(long id);
        void onError(Exception e);
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(Exception e);
    }

    /**
     * 训练数据类
     */
    public static class WorkoutData {
        public long id;
        public String date;
        public String bodyPart;
        public int intensity;
        public int duration;
        public String exercises;
        public String customExercises;
        
        public String getFormattedDate() {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                Date parsedDate = inputFormat.parse(date);
                return outputFormat.format(parsedDate);
            } catch (ParseException e) {
                return date;
            }
        }
        
        public String getIntensityDescription() {
            switch (intensity) {
                case WorkoutPlanManager.INTENSITY_VERY_LIGHT:
                    return "非常轻松";
                case WorkoutPlanManager.INTENSITY_LIGHT:
                    return "轻松";
                case WorkoutPlanManager.INTENSITY_MODERATE:
                    return "适中";
                case WorkoutPlanManager.INTENSITY_HARD:
                    return "困难";
                case WorkoutPlanManager.INTENSITY_VERY_HARD:
                    return "非常困难";
                default:
                    return "适中";
            }
        }
        
        public String getFormattedDuration() {
            int hours = duration / 60;
            int minutes = duration % 60;
            
            if (hours > 0) {
                return hours + "小时" + minutes + "分钟";
            } else {
                return minutes + "分钟";
            }
        }
        
        public Map<String, Integer> parseExercises() {
            Map<String, Integer> result = new HashMap<>();
            if (exercises == null || exercises.isEmpty()) {
                return result;
            }
            
            String[] items = exercises.split(";");
            for (String item : items) {
                String[] parts = item.split(":");
                if (parts.length == 2) {
                    try {
                        result.put(parts[0], Integer.parseInt(parts[1]));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing exercise sets: " + e.getMessage());
                    }
                }
            }
            
            return result;
        }
        
        public List<String> parseCustomExercises() {
            List<String> result = new ArrayList<>();
            if (customExercises == null || customExercises.isEmpty()) {
                return result;
            }
            
            String[] items = customExercises.split(";");
            for (String item : items) {
                if (!item.trim().isEmpty()) {
                    result.add(item.trim());
                }
            }
            
            return result;
        }
    }

    public void syncWithCloud(SyncCallback callback) {
        ApiService.syncFromServer(new ApiService.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    // 解析服务器返回的JSON数据
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        
                        WorkoutEntity workout = new WorkoutEntity();
                        workout.setDate(jsonObject.getString("date"));
                        workout.setBodyPart(jsonObject.getString("bodyPart"));
                        workout.setIntensity(jsonObject.getInt("intensity"));
                        workout.setDuration(jsonObject.getInt("duration"));
                        workout.setExercises(jsonObject.getString("exercises"));
                        workout.setCustomExercises(jsonObject.getString("customExercises"));
                        
                        // 保存到本地数据库
                        database.workoutDao().insert(workout);
                    }
                    
                    if (callback != null) {
                        callback.onSuccess();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "解析服务器数据失败", e);
                    if (callback != null) {
                        callback.onError(e);
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "从服务器同步失败", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    public interface SyncCallback {
        void onSuccess();
        void onError(Exception e);
    }
} 