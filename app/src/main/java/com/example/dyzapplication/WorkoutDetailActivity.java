package com.example.dyzapplication;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutDetailActivity extends AppCompatActivity {
    
    private TextView tvDate;
    private TextView tvBodyPart;
    private TextView tvIntensity;
    private TextView tvDuration;
    private ListView lvExercises;
    private ListView lvCustomExercises;
    private View customExercisesContainer;
    private Button btnDelete;
    
    private WorkoutRecord workoutRecord;
    private WorkoutPlanManager workoutPlanManager;
    private WorkoutRecord.WorkoutData workout;
    private long workoutId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_detail);
        
        // 启用 ActionBar 的返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // 获取传递的参数
        workoutId = getIntent().getLongExtra("WORKOUT_ID", -1);
        String workoutDate = getIntent().getStringExtra("WORKOUT_DATE");
        
        if (workoutId == -1 && workoutDate == null) {
            finish();
            return;
        }
        
        // 初始化控件
        tvDate = findViewById(R.id.tv_detail_date);
        tvBodyPart = findViewById(R.id.tv_detail_body_part);
        tvIntensity = findViewById(R.id.tv_detail_intensity);
        tvDuration = findViewById(R.id.tv_detail_duration);
        lvExercises = findViewById(R.id.lv_detail_exercises);
        lvCustomExercises = findViewById(R.id.lv_detail_custom_exercises);
        customExercisesContainer = findViewById(R.id.custom_exercises_detail_container);
        btnDelete = findViewById(R.id.btn_delete_workout);
        
        // 初始化管理器
        workoutRecord = new WorkoutRecord(this);
        workoutPlanManager = new WorkoutPlanManager(this);
        
        // 加载训练数据
        loadWorkoutData(workoutDate);
        
        // 设置删除按钮点击事件
        btnDelete.setOnClickListener(v -> confirmDelete());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 处理 ActionBar 的返回按钮点击事件
        if (item.getItemId() == android.R.id.home) {
            finish(); // 关闭当前 Activity，返回上一个界面
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 显示训练基本信息
     */
    private void displayWorkoutInfo() {
        tvDate.setText(workout.getFormattedDate());
        tvBodyPart.setText(workoutPlanManager.getBodyPartChineseName(workout.bodyPart));
        tvIntensity.setText(workout.getIntensityDescription());
        tvDuration.setText(workout.getFormattedDuration());
    }
    
    /**
     * 显示训练动作
     */
    private void displayExercises() {
        Map<String, Integer> exercises = workout.parseExercises();
        List<Map<String, String>> data = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : exercises.entrySet()) {
            Map<String, String> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("sets", entry.getValue() + " 组");
            data.add(item);
        }
        
        SimpleAdapter adapter = new SimpleAdapter(
                this, 
                data, 
                android.R.layout.simple_list_item_2,
                new String[]{"name", "sets"},
                new int[]{android.R.id.text1, android.R.id.text2});
        
        lvExercises.setAdapter(adapter);
    }
    
    /**
     * 显示自定义训练
     */
    private void displayCustomExercises() {
        List<String> customExercises = workout.parseCustomExercises();
        
        if (customExercises.isEmpty()) {
            customExercisesContainer.setVisibility(View.GONE);
            return;
        }
        
        customExercisesContainer.setVisibility(View.VISIBLE);
        
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                convertToMapList(customExercises),
                android.R.layout.simple_list_item_1,
                new String[]{"name"},
                new int[]{android.R.id.text1});
        
        lvCustomExercises.setAdapter(adapter);
    }
    
    /**
     * 转换字符串列表为Map列表
     */
    private List<Map<String, String>> convertToMapList(List<String> items) {
        List<Map<String, String>> result = new ArrayList<>();
        
        for (String item : items) {
            Map<String, String> map = new HashMap<>();
            map.put("name", item);
            result.add(map);
        }
        
        return result;
    }
    
    private void loadWorkoutData(String workoutDate) {
        workoutRecord.getWorkoutByDate(workoutDate, new WorkoutRecord.DataCallback<WorkoutRecord.WorkoutData>() {
            @Override
            public void onSuccess(WorkoutRecord.WorkoutData data) {
                if (data != null) {
                    workout = data;
                    runOnUiThread(() -> {
                        displayWorkoutInfo();
                        displayExercises();
                        displayCustomExercises();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(WorkoutDetailActivity.this, "未找到训练记录", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(WorkoutDetailActivity.this, "加载失败: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    
    /**
     * 确认删除
     */
    private void confirmDelete() {
        new AlertDialog.Builder(this)
            .setTitle("确认删除")
            .setMessage("确定要删除这条训练记录吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                workoutRecord.deleteWorkout(workout.id, new WorkoutRecord.Callback() {
                    @Override
                    public void onSuccess(long id) {
                        runOnUiThread(() -> {
                            Toast.makeText(WorkoutDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(WorkoutDetailActivity.this, "删除失败: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            })
            .setNegativeButton("取消", null)
            .show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (workoutRecord != null) {
            workoutRecord.close();
        }
    }
} 