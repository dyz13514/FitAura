package com.example.dyzapplication;

import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.Calendar;

public class IDActivity extends AppCompatActivity {
    private EditText userName, userAge, userBirth, userHobby;
    private TextView userOutput;
    private RadioGroup userSex;
    private Button BtSubmit, BtCancel;
    private String[] strHobby;
    private boolean[] checkedID;
    String StrSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);

        // 初始化 Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // 设置 Toolbar 作为 ActionBar

        // 控件初始化
        userName = findViewById(R.id.editname);
        userAge = findViewById(R.id.editage);
        userSex = findViewById(R.id.radiosex);
        userHobby = findViewById(R.id.edithobby);
        userBirth = findViewById(R.id.editbirth);
        userOutput = findViewById(R.id.text_output);
        BtSubmit = findViewById(R.id.button_submit);
        BtCancel = findViewById(R.id.button_cancel);

        strHobby = new String[]{"体育", "音乐", "旅行", "美术", "阅读"};
        checkedID = new boolean[strHobby.length];

        // 出生年月选择
        userBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(IDActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        userBirth.setText(year + "-" + (month + 1) + "-" + dayOfMonth); // 修正月份为从1开始
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // 操作性别选择控件
        userSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                StrSex = (checkedId == R.id.idmale) ? "男" : "女"; // 简化代码
            }
        });

        // 兴趣选择对话框
        userHobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myDialog = new AlertDialog.Builder(IDActivity.this);
                myDialog.setTitle("选择兴趣：");
                myDialog.setMultiChoiceItems(strHobby, checkedID, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedID[which] = isChecked;
                    }
                });
                myDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder text = new StringBuilder();
                        for (int i = 0; i < checkedID.length; i++) {
                            if (checkedID[i]) {
                                text.append(strHobby[i]).append("  ");
                            }
                        }
                        userHobby.setText(text.toString());
                    }
                });
                myDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userHobby.setText("");
                    }
                });
                myDialog.show();
            }
        });

        // 确定按钮操作
        BtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().trim().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(IDActivity.this);
                    builder.setTitle("警告").setMessage("请输入姓名！").setPositiveButton("确定", null).show();
                } else {
                    // 构建用户信息
                    StringBuilder userInfor = new StringBuilder()
                            .append(userName.getText().toString()).append("   ")
                            .append(userAge.getText().toString()).append("   ")
                            .append(userBirth.getText().toString()).append("   ")
                            .append(StrSex).append("   ")
                            .append(userHobby.getText().toString());
                    userOutput.setText(userInfor.toString());

                    // Notification 操作
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    String channelID = "RegResult";
                    int REG_NOTIFICATION_ID = 1000;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channelID, "注册信息", NotificationManager.IMPORTANCE_DEFAULT);
                        manager.createNotificationChannel(channel);
                    }

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelID)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("标题：注册成功！")
                            .setContentText(userOutput.getText().toString())
                            .setShowWhen(true)
                            .setAutoCancel(true)
                            .build();
                    manager.notify(REG_NOTIFICATION_ID, notification);
                }
            }
        });


        // 取消按钮操作
        BtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName.setText("");
                userAge.setText("");
                userBirth.setText("");
                userOutput.setText("");
                userSex.clearCheck();
                userHobby.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.regmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Drawable drawable;

        if (item.getItemId() == R.id.style_red) {
            drawable = ContextCompat.getDrawable(this, R.color.colorStRed); // 获取红色背景
            getWindow().setBackgroundDrawable(drawable);
            return true;
        }
        else if (item.getItemId() == R.id.style_green) {
            drawable = ContextCompat.getDrawable(this, R.color.colorStGreen); // 获取绿色背景
            getWindow().setBackgroundDrawable(drawable);
            return true;
        }
        else if (item.getItemId() == R.id.style_blue) {
            drawable = ContextCompat.getDrawable(this, R.color.colorStBlue); // 获取蓝色背景
            getWindow().setBackgroundDrawable(drawable);
            return true;
        }
        else if (item.getItemId() == R.id.app_version) {
            new AlertDialog.Builder(this)
                    .setTitle("版本信息")
                    .setMessage("开发者：戴元哲\n 日期：2024年10月31日")
                    .setPositiveButton("确定", null)
                    .show();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

}
