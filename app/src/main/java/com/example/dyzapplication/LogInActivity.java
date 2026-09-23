package com.example.dyzapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity {
    private EditText logUserName, logPassword;
    private Button btReg, btSub;
    private int LogResultCode = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        logUserName = findViewById(R.id.edit_username);
        logPassword = findViewById(R.id.edit_password);
        btReg = findViewById(R.id.button_reg);
        btSub = findViewById(R.id.button_submit);

        btSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (logUserName.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
                    builder.setTitle("警告")
                            .setMessage("请输入用户名")
                            .setPositiveButton("确定", null)
                            .show();
                } else {
                    // 查询SharedPreferences文件
                    SharedPreferences userInfo = getSharedPreferences("UserInfor", MODE_PRIVATE);
                    // 检查用户名和密码是否正确
                    if (userInfo.getString(logUserName.getText().toString(), "").equals(logPassword.getText().toString())) {
                        // 如果用户名和密码正确
                        SharedPreferences.Editor editor = getSharedPreferences("UserInfor", MODE_PRIVATE).edit();
                        editor.putString("logUser", logUserName.getText().toString()); // 设置当前已登录的用户
                        editor.apply();

                        Intent intent = new Intent();
                        intent.putExtra("username", logUserName.getText().toString()); // 将用户名作为额外信息放入Intent
                        setResult(LogResultCode, intent);
                        finish(); // 结束当前Activity
                    } else {
                        // 如果用户名和密码错误
                        Toast.makeText(getApplicationContext(), "用户名和密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this,RegActivity.class);
                startActivity(intent);
            }
        });
    }
}