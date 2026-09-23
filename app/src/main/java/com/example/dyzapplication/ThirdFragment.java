package com.example.dyzapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ThirdFragment extends Fragment {
    
    private CalendarView calendarView;
    private TextView tvNoWorkout;
    private ListView listWorkouts;
    private WorkoutRecord workoutRecord;
    private List<WorkoutRecord.WorkoutData> allWorkouts = new ArrayList<>();
    private WorkoutAdapter adapter;
    private FloatingActionButton fabAddWorkout;
    private String selectedDate;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        
        // 初始化控件
        calendarView = view.findViewById(R.id.calendar_view);
        tvNoWorkout = view.findViewById(R.id.tv_no_workout);
        listWorkouts = view.findViewById(R.id.list_workouts);
        fabAddWorkout = view.findViewById(R.id.fab_add_workout);
        
        // 初始化训练记录管理器
        workoutRecord = new WorkoutRecord(getActivity());
        
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate = year + "-" + month + "-" + day;
        
        // 加载所有训练记录
        loadWorkouts();
        
        // 设置日历点击事件
        calendarView.setOnDateChangeListener((view1, year1, month1, dayOfMonth) -> {
            // 构建日期字符串 (yyyy-M-d)
            selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            
            // 查找该日期的训练记录
            displayWorkoutForDate(selectedDate);
        });
        
        // 设置列表项点击事件
        listWorkouts.setOnItemClickListener((parent, view12, position, id) -> {
            // 获取点击的训练记录
            WorkoutRecord.WorkoutData workout = allWorkouts.get(position);
            
            // 打开详情页面
            Intent intent = new Intent(getActivity(), WorkoutDetailActivity.class);
            intent.putExtra("WORKOUT_ID", workout.id);
            intent.putExtra("WORKOUT_DATE", workout.date);
            startActivity(intent);
        });
        
        // 设置添加训练按钮
        fabAddWorkout.setOnClickListener(v -> {
            // 打开训练活动页面，传入选中的日期
            Intent intent = new Intent(getActivity(), WorkoutActivity.class);
            intent.putExtra("SELECTED_DATE", selectedDate);
            startActivity(intent);
        });
        
        return view;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // 刷新训练数据
        loadWorkouts();
    }
    
    /**
     * 刷新训练数据
     */
    private void loadWorkouts() {
        workoutRecord.getAllWorkouts(new WorkoutRecord.DataCallback<List<WorkoutRecord.WorkoutData>>() {
            @Override
            public void onSuccess(List<WorkoutRecord.WorkoutData> workouts) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        allWorkouts = workouts;
                        displayWorkoutForDate(selectedDate);
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getActivity(), "加载失败: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
    
    /**
     * 显示指定日期的训练记录
     */
    private void displayWorkoutForDate(String dateStr) {
        // 查找该日期的训练记录
        workoutRecord.getWorkoutByDate(dateStr, new WorkoutRecord.DataCallback<WorkoutRecord.WorkoutData>() {
            @Override
            public void onSuccess(WorkoutRecord.WorkoutData workout) {
                if (getActivity() == null) return;
                
                getActivity().runOnUiThread(() -> {
                    if (workout != null) {
                        // 显示训练记录
                        tvNoWorkout.setVisibility(View.GONE);
                        listWorkouts.setVisibility(View.VISIBLE);
                        
                        // 更新allWorkouts列表
                        allWorkouts.clear();
                        allWorkouts.add(workout);
                        
                        adapter = new WorkoutAdapter(allWorkouts);
                        listWorkouts.setAdapter(adapter);
                    } else {
                        // 显示无训练记录提示
                        tvNoWorkout.setVisibility(View.VISIBLE);
                        listWorkouts.setVisibility(View.GONE);
                        
                        try {
                            // 格式化日期供显示
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                            Date date = inputFormat.parse(dateStr);
                            String formattedDate = outputFormat.format(date);
                            
                            tvNoWorkout.setText(formattedDate + "\n\n没有训练记录\n\n点击右下角按钮添加训练");
                        } catch (ParseException e) {
                            tvNoWorkout.setText("没有训练记录\n\n点击右下角按钮添加训练");
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() == null) return;
                
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "加载训练记录失败: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (workoutRecord != null) {
            workoutRecord.close();
        }
    }
    
    /**
     * 训练记录适配器
     */
    private class WorkoutAdapter extends ArrayAdapter<WorkoutRecord.WorkoutData> {
        
        public WorkoutAdapter(List<WorkoutRecord.WorkoutData> workouts) {
            super(getActivity(), 0, workouts);
        }
        
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.workout_list_item, parent, false);
            }
            
            WorkoutRecord.WorkoutData workout = getItem(position);
            if (workout == null) return convertView;
            
            // 获取视图控件
            TextView tvDate = convertView.findViewById(R.id.tv_workout_date);
            TextView tvBodyPart = convertView.findViewById(R.id.tv_workout_body_part);
            TextView tvIntensity = convertView.findViewById(R.id.tv_workout_intensity);
            TextView tvDuration = convertView.findViewById(R.id.tv_workout_duration);
            
            // 设置数据
            tvDate.setText(workout.getFormattedDate());
            tvBodyPart.setText("训练部位: " + workoutPlanManager().getBodyPartChineseName(workout.bodyPart));
            tvIntensity.setText("训练强度: " + workout.getIntensityDescription());
            tvDuration.setText("训练时长: " + workout.getFormattedDuration());
            
            return convertView;
        }
    }
    
    /**
     * 获取WorkoutPlanManager实例
     */
    private WorkoutPlanManager workoutPlanManager() {
        return new WorkoutPlanManager(getActivity());
    }
}
