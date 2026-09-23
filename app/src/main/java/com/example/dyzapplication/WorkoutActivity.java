package com.example.dyzapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.ParseException;

public class WorkoutActivity extends AppCompatActivity {

    private ListView listView;
    private Button btnBack;
    private Button btnComplete;
    private Button btnAddExercise;
    private TextView tvDate;
    private TextView tvBodyPart;
    private TextView tvIntensity;
    private Chronometer chronometer;
    private Button btnStartPause;
    
    private String bodyPart;
    private int intensity;
    private boolean isCustom;
    private WorkoutPlanManager workoutPlanManager;
    private List<WorkoutExercise> exercises = new ArrayList<>();
    private List<String> customExercises = new ArrayList<>();
    private ExerciseAdapter adapter;
    private WorkoutRecord workoutRecord;
    
    private boolean isTimerRunning = false;
    private long pauseOffset = 0;
    private long startTime = 0;
    private String workoutDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // 初始化控件
        listView = findViewById(R.id.lv_exercises);
        btnBack = findViewById(R.id.btn_back);
        btnComplete = findViewById(R.id.btn_complete);
        btnAddExercise = findViewById(R.id.btn_add_exercise);
        tvDate = findViewById(R.id.tv_date);
        tvBodyPart = findViewById(R.id.tv_body_part);
        tvIntensity = findViewById(R.id.tv_intensity);
        chronometer = findViewById(R.id.chronometer);
        btnStartPause = findViewById(R.id.btn_start_pause);

        // 初始化工作管理器和记录器
        workoutPlanManager = new WorkoutPlanManager(this);
        workoutRecord = new WorkoutRecord(this);

        // 获取传递的参数
        bodyPart = getIntent().getStringExtra("BODY_PART");
        intensity = getIntent().getIntExtra("INTENSITY", WorkoutPlanManager.INTENSITY_MODERATE);
        isCustom = getIntent().getBooleanExtra("IS_CUSTOM", false);
        
        // 检查是否从ThirdFragment传递了日期
        if (getIntent().hasExtra("SELECTED_DATE")) {
            workoutDate = getIntent().getStringExtra("SELECTED_DATE");
            // 使用传递的日期更新UI
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                Date date = inputFormat.parse(workoutDate);
                tvDate.setText(outputFormat.format(date));
            } catch (ParseException e) {
                // 如果解析失败，回退到当前日期
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                tvDate.setText(sdf.format(new Date()));
                // 同时也要设置workoutDate为标准格式
                SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                workoutDate = standardFormat.format(new Date());
            }
        } else {
            // 设置日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            tvDate.setText(sdf.format(new Date()));
            SimpleDateFormat standardFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
            workoutDate = standardFormat.format(new Date());
        }

        // 设置训练部位
        if (bodyPart != null) {
            String bodyPartName = workoutPlanManager.getBodyPartChineseName(bodyPart);
            tvBodyPart.setText("训练部位: " + bodyPartName);
        } else {
            tvBodyPart.setText("训练部位: 未指定");
            Toast.makeText(this, "未指定训练部位，请先选择", Toast.LENGTH_LONG).show();
        }

        // 设置强度显示
        updateIntensityDisplay();

        // 加载训练动作
        loadExercises();

        // 设置列表适配器
        adapter = new ExerciseAdapter();
        listView.setAdapter(adapter);

        // 计时器设置
        chronometer.setFormat("训练时间: %s");
        btnStartPause.setOnClickListener(v -> toggleTimer());

        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> {
            if (isTimerRunning) {
                showExitConfirmDialog();
            } else {
                finish();
            }
        });

        // 完成训练按钮点击事件
        btnComplete.setOnClickListener(v -> {
            if (isAllExercisesCompleted()) {
                showIntensityDialog();
            } else {
                showConfirmDialog();
            }
        });
        
        // 添加自定义训练按钮
        btnAddExercise.setOnClickListener(v -> showAddExerciseDialog());
    }
    
    @Override
    public void onBackPressed() {
        if (isTimerRunning) {
            showExitConfirmDialog();
        } else {
            super.onBackPressed();
        }
    }
    
    /**
     * 开始/暂停计时器
     */
    private void toggleTimer() {
        if (isTimerRunning) {
            // 暂停计时器
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            btnStartPause.setText("继续");
            isTimerRunning = false;
        } else {
            // 开始/继续计时器
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            btnStartPause.setText("暂停");
            isTimerRunning = true;
            
            // 记录开始时间（如果是第一次开始）
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
        }
    }
    
    /**
     * 显示退出确认对话框
     */
    private void showExitConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("训练正在进行");
        builder.setMessage("确定要退出吗？训练数据将不会被保存。");
        builder.setPositiveButton("退出", (dialog, which) -> finish());
        builder.setNegativeButton("继续训练", null);
        builder.show();
    }
    
    /**
     * 显示添加自定义训练对话框
     */
    private void showAddExerciseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加自定义训练");
        
        // 创建输入框
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("输入训练动作名称");
        builder.setView(input);
        
        builder.setPositiveButton("添加", (dialog, which) -> {
            String exerciseName = input.getText().toString().trim();
            if (!exerciseName.isEmpty()) {
                // 添加到自定义训练列表
                customExercises.add(exerciseName);
                Toast.makeText(WorkoutActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                
                // 更新显示
                showCustomExercises();
            }
        });
        
        builder.setNegativeButton("取消", null);
        builder.show();
    }
    
    /**
     * 显示自定义训练
     */
    private void showCustomExercises() {
        if (customExercises.isEmpty()) {
            return;
        }
        
        // 创建自定义训练视图
        View customView = findViewById(R.id.custom_exercises_container);
        if (customView.getVisibility() == View.GONE) {
            customView.setVisibility(View.VISIBLE);
        }
        
        // 更新自定义训练列表
        ListView customListView = findViewById(R.id.lv_custom_exercises);
        ArrayAdapter<String> customAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_list_item_1, customExercises);
        customListView.setAdapter(customAdapter);
    }
    
    /**
     * 更新强度显示
     */
    private void updateIntensityDisplay() {
        String intensityDesc = workoutPlanManager.getIntensityDescription(intensity);
        tvIntensity.setText("训练强度: " + intensityDesc);
    }

    /**
     * 加载训练动作
     */
    private void loadExercises() {
        // 获取动作和组数
        // Map<String, Integer> exercisesWithSets = workoutPlanManager.getExercisesWithSets(bodyPart, intensity);
        exercises.clear(); // 清空列表

        if (bodyPart != null) {
            Map<String, Integer> exercisesWithSets = workoutPlanManager.getExercisesWithSets(bodyPart, intensity);
            // 添加动作到列表
            for (Map.Entry<String, Integer> entry : exercisesWithSets.entrySet()) {
                exercises.add(new WorkoutExercise(entry.getKey(), entry.getValue()));
            }
        } else {
            // 如果 bodyPart 为 null，不加载任何预设动作
            // 可以在这里提示用户，或者允许用户通过"添加自定义训练"来添加动作
            // Toast.makeText(this, "请先选择训练部位来加载预设计划", Toast.LENGTH_LONG).show(); 
            //  ^ 如果取消注释，每次没有bodyPart都会有提示，可能会有点烦人
        }
        
        // 清空列表 (这行似乎是多余的，因为前面已经清空过了，而且如果 bodyPart != null，这里会清空刚加载的动作)
        // exercises.clear(); 
        
        // 添加动作到列表 (这部分逻辑与上面的重复了，且没有判断 bodyPart 是否为 null)
        // for (Map.Entry<String, Integer> entry : exercisesWithSets.entrySet()) { // exercisesWithSets 在 bodyPart 为 null 时未定义
        //    exercises.add(new WorkoutExercise(entry.getKey(), entry.getValue()));
        // }
    }

    /**
     * 检查是否所有动作都已完成
     */
    private boolean isAllExercisesCompleted() {
        for (WorkoutExercise exercise : exercises) {
            if (!exercise.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示确认对话框（当有未完成的动作时）
     */
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("训练未完成");
        builder.setMessage("有训练动作尚未完成，确定要结束训练吗？");
        builder.setPositiveButton("确定", (dialog, which) -> showIntensityDialog());
        builder.setNegativeButton("继续训练", null);
        builder.show();
    }

    /**
     * 显示强度询问对话框
     */
    private void showIntensityDialog() {
        // 停止计时器
        if (isTimerRunning) {
            chronometer.stop();
            isTimerRunning = false;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_intensity, null);
        builder.setView(view);
        
        TextView tvCurrentIntensity = view.findViewById(R.id.tv_current_intensity);
        SeekBar seekBar = view.findViewById(R.id.seekbar_intensity);
        TextView tvIntensityDesc = view.findViewById(R.id.tv_intensity_desc);
        
        // 设置初始值
        seekBar.setProgress(intensity - 1);  // SeekBar从0开始，我们的强度从1开始
        updateIntensityDescription(tvIntensityDesc, intensity);
        tvCurrentIntensity.setText("当前强度: " + workoutPlanManager.getIntensityDescription(intensity));
        
        // 监听滑动条变化
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int newIntensity = progress + 1;  // 转换回1-5的范围
                updateIntensityDescription(tvIntensityDesc, newIntensity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        
        builder.setTitle("今日训练强度如何？");
        builder.setPositiveButton("保存", (dialog, which) -> {
            int newIntensity = seekBar.getProgress() + 1;
            saveWorkoutRecord(newIntensity);
            finish();
        });
        
        builder.setNegativeButton("取消", null);
        builder.show();
    }
    
    /**
     * 保存训练记录
     */
    private void saveWorkoutRecord(int finalIntensity) {
        // 计算训练时长（分钟）
        int durationMinutes = 0;
        if (startTime > 0) {
            long elapsedMillis = (isTimerRunning ? 
                    SystemClock.elapsedRealtime() : SystemClock.elapsedRealtime() - pauseOffset) - 
                    chronometer.getBase();
            durationMinutes = (int) (elapsedMillis / 60000);
        }
        
        // 格式化训练动作和组数
        StringBuilder exercisesStr = new StringBuilder();
        for (WorkoutExercise exercise : exercises) {
            if (exercisesStr.length() > 0) {
                exercisesStr.append(";");
            }
            exercisesStr.append(exercise.getName()).append(":").append(exercise.getSets());
        }
        
        // 格式化自定义训练
        StringBuilder customExercisesStr = new StringBuilder();
        for (String exercise : customExercises) {
            if (customExercisesStr.length() > 0) {
                customExercisesStr.append(";");
            }
            customExercisesStr.append(exercise);
        }
        
        // 使用成员变量中的日期
        String dateStr = workoutDate;
        
        // 保存到数据库
        workoutRecord.saveWorkout(
                dateStr, 
                bodyPart, 
                finalIntensity, 
                durationMinutes, 
                exercisesStr.toString(), 
                customExercisesStr.toString(),
                new WorkoutRecord.Callback() {
                    @Override
                    public void onSuccess(long id) {
                        runOnUiThread(() -> {
                            Toast.makeText(WorkoutActivity.this, "训练已记录", Toast.LENGTH_SHORT).show();
                            // 只有非自定义训练才会记录到计划中
                            if (!isCustom) {
                                workoutPlanManager.recordWorkout(bodyPart, finalIntensity);
                            }
                            finish();
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> {
                            Toast.makeText(WorkoutActivity.this, "保存失败: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
    
    /**
     * 更新强度描述文本
     */
    private void updateIntensityDescription(TextView textView, int intensity) {
        String description = workoutPlanManager.getIntensityDescription(intensity);
        textView.setText(description);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (workoutRecord != null) {
            workoutRecord.close();
        }
    }
    
    /**
     * 训练动作类
     */
    private static class WorkoutExercise {
        private String name;
        private int sets;
        private boolean completed;
        
        public WorkoutExercise(String name, int sets) {
            this.name = name;
            this.sets = sets;
            this.completed = false;
        }
        
        public String getName() {
            return name;
        }
        
        public int getSets() {
            return sets;
        }
        
        public void setSets(int sets) {
            this.sets = sets;
        }
        
        public boolean isCompleted() {
            return completed;
        }
        
        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
    
    /**
     * 训练适配器
     */
    private class ExerciseAdapter extends ArrayAdapter<WorkoutExercise> {
        
        public ExerciseAdapter() {
            super(WorkoutActivity.this, R.layout.exercise_item, exercises);
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.exercise_item, parent, false);
            }
            
            WorkoutExercise exercise = exercises.get(position);
            
            TextView tvExerciseName = convertView.findViewById(R.id.tv_exercise_name);
            CheckBox cbComplete = convertView.findViewById(R.id.cb_complete);
            TextView tvSets = convertView.findViewById(R.id.tv_sets);
            Button btnEditSets = convertView.findViewById(R.id.btn_edit_sets);
            
            tvExerciseName.setText(exercise.getName());
            tvSets.setText(exercise.getSets() + " 组");
            cbComplete.setChecked(exercise.isCompleted());
            
            cbComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                exercise.setCompleted(isChecked);
            });
            
            btnEditSets.setOnClickListener(v -> showEditSetsDialog(position));
            
            return convertView;
        }
    }
    
    /**
     * 显示编辑组数对话框
     */
    private void showEditSetsDialog(int position) {
        WorkoutExercise exercise = exercises.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("编辑组数: " + exercise.getName());

        View view = getLayoutInflater().inflate(R.layout.dialog_number_picker, null);
        NumberPicker numberPicker = view.findViewById(R.id.number_picker);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(exercise.getSets());

        builder.setView(view);

        builder.setPositiveButton("确定", (dialog, which) -> {
            int newSets = numberPicker.getValue();
            exercise.setSets(newSets);
            adapter.notifyDataSetChanged();
            Toast.makeText(WorkoutActivity.this, "组数已更新", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }
}