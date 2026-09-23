package com.example.dyzapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class RegActivity extends AppCompatActivity {

    private EditText regUserName, regPW, regPwconfirm;
    private Button regSubmit, regCancle;
    private void sendRegistrationSuccessNotification() {
        int NOTIFICATION_ID = 1;
        String CHANNEL_ID = "channel_1";
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelID = "RegResult";
        int REG_NOTIFICATION_ID = 100;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, "注册信息", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("标题：注册成功！")
                .setContentText("您的账户已成功注册！")
                .setShowWhen(true)
                .setAutoCancel(true)
                .build();
        manager.notify(REG_NOTIFICATION_ID, notification);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        regUserName = findViewById(R.id.regedit_username);
        regPW = findViewById(R.id.regedit_password);
        regPwconfirm = findViewById(R.id.regedit_comformpassword);
        regSubmit = findViewById(R.id.regbutton_reg);
        regCancle = findViewById(R.id.regutton_cancel);

        regSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (regUserName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (regPW.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (!regPW.getText().toString().equals(regPwconfirm.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "两次输入密码不相同", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("UserInfor", MODE_PRIVATE).edit();
                    editor.putString(regUserName.getText().toString(), regPW.getText().toString());
                    editor.apply();
                    sendRegistrationSuccessNotification();
                    finish();
                }
            }
        });

        regCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}