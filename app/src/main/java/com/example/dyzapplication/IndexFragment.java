package com.example.dyzapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IndexFragment extends Fragment {

    private TextView tvDate;
    private TextView tvBodyPart;
    private TextView tvWorkoutStatus;
    private Button btnTodayWorkout;
    private Button btnCustomWorkout;
    private WorkoutPlanManager workoutPlanManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_index, container, false);

        // 初始化控件
        tvDate = view.findViewById(R.id.tv_date);
        tvBodyPart = view.findViewById(R.id.tv_body_part);
        tvWorkoutStatus = view.findViewById(R.id.tv_workout_status);
        btnTodayWorkout = view.findViewById(R.id.btn_today_workout);
        btnCustomWorkout = view.findViewById(R.id.btn_custom_workout);

        // 初始化健身计划管理器
        workoutPlanManager = new WorkoutPlanManager(getActivity());

        // 设置日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        tvDate.setText(sdf.format(new Date()));

        // 更新UI
        updateUI();

        // 今日训练按钮点击事件
        btnTodayWorkout.setOnClickListener(v -> {
            String bodyPart = workoutPlanManager.getTodayBodyPart();
            Intent intent = new Intent(getActivity(), WorkoutActivity.class);
            intent.putExtra("BODY_PART", bodyPart);
            intent.putExtra("INTENSITY", workoutPlanManager.getRecommendedIntensity());
            startActivity(intent);
        });

        // 自定义训练按钮点击事件
        btnCustomWorkout.setOnClickListener(v -> showCustomWorkoutDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    /**
     * 更新UI显示
     */
    private void updateUI() {
        String bodyPart = workoutPlanManager.getTodayBodyPart();
        String bodyPartName = workoutPlanManager.getBodyPartChineseName(bodyPart);
        
        tvBodyPart.setText("今日训练部位: " + bodyPartName);
        
        if (workoutPlanManager.isTodayWorkoutCompleted()) {
            tvWorkoutStatus.setText("状态: 已完成");
            tvWorkoutStatus.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark));
            btnTodayWorkout.setText("查看今日训练");
        } else {
            tvWorkoutStatus.setText("状态: 未完成");
            tvWorkoutStatus.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark));
            btnTodayWorkout.setText("开始今日训练");
        }
    }

    /**
     * 显示自定义训练对话框
     */
    private void showCustomWorkoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("选择训练部位");
        
        final String[] bodyParts = {"肩部", "背部", "胸部", "腿部"};
        final String[] bodyPartKeys = {"shoulders", "back", "chest", "legs"};
        
        builder.setItems(bodyParts, (dialog, which) -> {
            Intent intent = new Intent(getActivity(), WorkoutActivity.class);
            intent.putExtra("BODY_PART", bodyPartKeys[which]);
            intent.putExtra("INTENSITY", workoutPlanManager.getRecommendedIntensity());
            intent.putExtra("IS_CUSTOM", true);
            startActivity(intent);
        });
        
        builder.show();
    }
}



