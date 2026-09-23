package com.example.dyzapplication;

import android.content.Context;
import android.util.Log;
import com.example.dyzapplication.database.WorkoutDatabase;
import com.example.dyzapplication.database.PlanEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlanManager {
    private static final String TAG = "PlanManager";
    private final Context context;
    private final WorkoutDatabase database;
    private final ExecutorService executorService;
    private final String userName;

    public PlanManager(Context context, String userName) {
        this.context = context;
        this.database = WorkoutDatabase.getInstance(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.userName = userName;
    }

    public void savePlan(String title, String content, String date, String time, Callback callback) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Attempting to save plan for user: " + userName);
                Log.d(TAG, "Plan details - Title: " + title + ", Content: " + content + ", Date: " + date + ", Time: " + time);

                PlanEntity plan = new PlanEntity();
                plan.setTitle(title);
                plan.setContent(content);
                plan.setDate(date);
                plan.setTime(time);
                plan.setState("待办");
                plan.setUserName(userName);

                long id = database.planDao().insert(plan);
                Log.d(TAG, "Plan saved successfully with id: " + id);
                if (callback != null) {
                    callback.onSuccess(id);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error saving plan: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    public void getAllPlans(DataCallback<List<PlanEntity>> callback) {
        executorService.execute(() -> {
            try {
                List<PlanEntity> plans = database.planDao().getAllPlans(userName);
                callback.onSuccess(plans);
            } catch (Exception e) {
                Log.e(TAG, "Error getting plans: " + e.getMessage());
                callback.onError(e);
            }
        });
    }

    public void getPlansByDate(String date, DataCallback<List<PlanEntity>> callback) {
        executorService.execute(() -> {
            try {
                List<PlanEntity> plans = database.planDao().getPlansByDate(userName, date);
                callback.onSuccess(plans);
            } catch (Exception e) {
                Log.e(TAG, "Error getting plans by date: " + e.getMessage());
                callback.onError(e);
            }
        });
    }

    public void deletePlan(long id, Callback callback) {
        executorService.execute(() -> {
            try {
                PlanEntity plan = database.planDao().getPlanById(id);
                if (plan != null) {
                    database.planDao().delete(plan);
                    if (callback != null) {
                        callback.onSuccess(id);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting plan: " + e.getMessage());
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
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
} 