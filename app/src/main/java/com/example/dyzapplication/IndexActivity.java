package com.example.dyzapplication;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class IndexActivity extends AppCompatActivity {

    private EditText userName, userAge, userBirth, userHeight, userWeight, userHobby;
    private TextView userOutput;
    private RadioGroup userSex;
    private Button BtSubmit, BtCancel;
    private String[] strHobby;
    private boolean[] checkedID;
    private String StrSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // 初始化控件
        userName = findViewById(R.id.editname);
        userAge = findViewById(R.id.editage);
        userSex = findViewById(R.id.radiosex);
        userHobby = findViewById(R.id.edithobby);
        userBirth = findViewById(R.id.editbirth);
        userHeight = findViewById(R.id.editheight);
        userWeight = findViewById(R.id.editweight);
        userOutput = findViewById(R.id.text_output);
        BtSubmit = findViewById(R.id.button_submit);
        BtCancel = findViewById(R.id.button_cancel);

        strHobby = new String[]{"基础锻炼", "减脂", "增肌", "塑形", "耐力"};
        checkedID = new boolean[strHobby.length];

        // 出生年月选择
        userBirth.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(IndexActivity.this, (view, year, month, dayOfMonth) -> {
                userBirth.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // 身高选择（50-250 cm，每 0.5 cm 一格）
        userHeight.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(IndexActivity.this);
            builder.setTitle("选择身高 (cm)");

            final NumberPicker numberPicker = new NumberPicker(IndexActivity.this);
            String[] heightValues = new String[401]; // 50 到 250，步长 0.5，共 401 个值
            for (int i = 0; i < heightValues.length; i++) {
                heightValues[i] = String.format("%.1f", 50.0 + i * 0.5);
            }
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(heightValues.length - 1);
            numberPicker.setDisplayedValues(heightValues);
            numberPicker.setValue(200); // 默认值 150 cm (索引 200 = 50 + 200 * 0.5)
            numberPicker.setWrapSelectorWheel(false);

            builder.setView(numberPicker);
            builder.setPositiveButton("确定", (dialog, which) -> {
                userHeight.setText(heightValues[numberPicker.getValue()]);
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        });

        // 体重选择（20-200 kg，每 0.5 kg 一格）
        userWeight.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(IndexActivity.this);
            builder.setTitle("选择体重 (kg)");

            final NumberPicker numberPicker = new NumberPicker(IndexActivity.this);
            String[] weightValues = new String[361]; // 20 到 200，步长 0.5，共 361 个值
            for (int i = 0; i < weightValues.length; i++) {
                weightValues[i] = String.format("%.1f", 20.0 + i * 0.5);
            }
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(weightValues.length - 1);
            numberPicker.setDisplayedValues(weightValues);
            numberPicker.setValue(100); // 默认值 70 kg (索引 100 = 20 + 100 * 0.5)
            numberPicker.setWrapSelectorWheel(false);

            builder.setView(numberPicker);
            builder.setPositiveButton("确定", (dialog, which) -> {
                userWeight.setText(weightValues[numberPicker.getValue()]);
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        });

        // 操作性别选择控件
        userSex.setOnCheckedChangeListener((group, checkedId) -> {
            StrSex = (checkedId == R.id.idmale) ? "男" : "女";
        });

        // 兴趣选择对话框
        userHobby.setOnClickListener(v -> {
            AlertDialog.Builder myDialog = new AlertDialog.Builder(IndexActivity.this);
            myDialog.setTitle("选择偏好：");
            myDialog.setMultiChoiceItems(strHobby, checkedID, (dialog, which, isChecked) -> checkedID[which] = isChecked);
            myDialog.setPositiveButton("确定", (dialog, which) -> {
                StringBuilder text = new StringBuilder();
                for (int i = 0; i < checkedID.length; i++) {
                    if (checkedID[i]) {
                        text.append(strHobby[i]).append("  ");
                    }
                }
                userHobby.setText(text.toString());
            });
            myDialog.setNegativeButton("取消", (dialog, which) -> userHobby.setText(""));
            myDialog.show();
        });

        // 确定按钮操作
        BtSubmit.setOnClickListener(v -> {
            if (userName.getText().toString().trim().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(IndexActivity.this);
                builder.setTitle("警告").setMessage("请输入姓名！").setPositiveButton("确定", null).show();
            } else {
                String name = userName.getText().toString();
                String age = userAge.getText().toString();
                String birth = userBirth.getText().toString();
                String sex = StrSex != null ? StrSex : ""; // 确保 StrSex 不是 null
                String height = userHeight.getText().toString();
                String weight = userWeight.getText().toString();
                String hobby = userHobby.getText().toString();

                StringBuilder userInforBuilder = new StringBuilder()
                        .append("姓名: ").append(name).append("\\n")
                        .append("年龄: ").append(age).append("\\n")
                        .append("生日: ").append(birth).append("\\n")
                        .append("性别: ").append(sex).append("\\n")
                        .append("身高: ").append(height).append(" cm\\n")
                        .append("体重: ").append(weight).append(" kg\\n")
                        .append("偏好: ").append(hobby);
                String userInforString = userInforBuilder.toString();
                userOutput.setText("注册成功！");

                // 保存用户信息到 SharedPreferences
                SharedPreferences spUserInfor = getSharedPreferences("UserInfor", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = spUserInfor.edit();
                // 使用当前登录的用户名作为键名的一部分，如果当前没有登录用户，则使用默认键名
                String currentUsername = spUserInfor.getString("logUser", "");
                String userInfoKey = !currentUsername.isEmpty() ? currentUsername + "_index_info" : "index_info";
                editor.putString(userInfoKey, userInforString);
                editor.apply();
                
                // 设置返回结果，通知 FourthFragment 更新
                setResult(5); // 使用与 FourthFragment 中相同的 resultCode

                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String channelID = "RegResult";
                int REG_NOTIFICATION_ID = 1000;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelID, "注册信息", NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(channel);
                }

                Notification notification = new NotificationCompat.Builder(this, channelID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("标题：注册成功！")
                        .setContentText(userOutput.getText().toString())
                        .setShowWhen(true)
                        .setAutoCancel(true)
                        .build();
                manager.notify(REG_NOTIFICATION_ID, notification);
            }
        });

        // 取消按钮操作
        BtCancel.setOnClickListener(v -> {
            userName.setText("");
            userAge.setText("");
            userBirth.setText("");
            userHeight.setText(""); // 清空身高
            userWeight.setText(""); // 清空体重
            userOutput.setText("");
            userSex.clearCheck();
            userHobby.setText("");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.regmenu, menu);

        // 为子菜单项设置点击监听器
        MenuItem styleMenu = menu.findItem(R.id.activity_style);
        if (styleMenu != null) {
            Menu subMenu = styleMenu.getSubMenu();
            if (subMenu != null) {
                subMenu.findItem(R.id.style_red).setOnMenuItemClickListener(item -> {
                    Drawable drawable = ContextCompat.getDrawable(this, R.color.colorStRed);
                    getWindow().setBackgroundDrawable(drawable);
                    return true;
                });
                subMenu.findItem(R.id.style_green).setOnMenuItemClickListener(item -> {
                    Drawable drawable = ContextCompat.getDrawable(this, R.color.colorStGreen);
                    getWindow().setBackgroundDrawable(drawable);
                    return true;
                });
                subMenu.findItem(R.id.style_blue).setOnMenuItemClickListener(item -> {
                    Drawable drawable = ContextCompat.getDrawable(this, R.color.colorStBlue);
                    getWindow().setBackgroundDrawable(drawable);
                    return true;
                });
                subMenu.findItem(R.id.style_ori).setOnMenuItemClickListener(item -> {
                    Drawable drawable = ContextCompat.getDrawable(this, R.color.white);
                    getWindow().setBackgroundDrawable(drawable);
                    return true;
                });
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.app_version) {
            new AlertDialog.Builder(this)
                    .setTitle("版本信息")
                    .setMessage("版本：2.0\n 开发者：戴元哲\n 日期：2024年12月31日")
                    .setPositiveButton("确定", null)
                    .show();
            return true;
        }
        // 如果是 activity_style，交给系统处理（展开子菜单）
        return super.onOptionsItemSelected(item);
    }
}