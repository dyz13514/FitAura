package com.example.dyzapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class PlanActivity extends AppCompatActivity {
    private EditText et_title, et_content, et_date, et_time;
    private Button bt_clear, bt_insert, bt_query;
    private TextView tv_Infor;
    private String userName;
    private PlanManager planManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        // 启用 ActionBar 的返回按钮
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // 初始化组件
        et_title = findViewById(R.id.et_agendaTitle);
        et_content = findViewById(R.id.et_agendaContent);
        et_date = findViewById(R.id.et_agendaDate);
        et_time = findViewById(R.id.et_agendaTime);
        bt_clear = findViewById(R.id.bt_clean);
        bt_insert = findViewById(R.id.bt_insert);
        bt_query = findViewById(R.id.bt_query);
        tv_Infor = findViewById(R.id.tv_infor);

        // 读取用户登录信息
        SharedPreferences userInfo = getSharedPreferences("UserInfor", MODE_PRIVATE);
        userName = userInfo.getString("LogUser", "");

        // 初始化计划管理器
        planManager = new PlanManager(this, userName);

        // 设置日期选择器
        et_date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                PlanActivity.this,
                (view, year, month, dayOfMonth) -> 
                    et_date.setText(year + "-" + (month + 1) + "-" + dayOfMonth),
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // 设置时间选择器
        et_time.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                PlanActivity.this,
                (view, hourOfDay, minute) -> 
                    et_time.setText(String.format("%02d:%02d", hourOfDay, minute)),
                0, 0, true
            );
            timePickerDialog.show();
        });

        // 插入计划
        bt_insert.setOnClickListener(v -> {
            String title = et_title.getText().toString();
            String content = et_content.getText().toString();
            String date = et_date.getText().toString();
            String time = et_time.getText().toString();

            if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                tv_Infor.setText("请填写完整信息！");
                return;
            }

            planManager.savePlan(title, content, date, time, new PlanManager.Callback() {
                @Override
                public void onSuccess(long id) {
                    runOnUiThread(() -> 
                        tv_Infor.setText("创建一条新日程:\n" + date + " " + time + " " + title)
                    );
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> 
                        tv_Infor.setText("日程创建失败: " + e.getMessage())
                    );
                }
            });
        });

        // 跳转到查询界面
        bt_query.setOnClickListener(v -> {
            Intent intent = new Intent(PlanActivity.this, PlanListActivity.class);
            intent.putExtra("userName", userName);
            startActivity(intent);
        });

        // 清空输入字段
        bt_clear.setOnClickListener(v -> {
            et_title.setText("");
            et_content.setText("");
            et_date.setText("");
            et_time.setText("");
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (planManager != null) {
            planManager.close();
        }
    }
}