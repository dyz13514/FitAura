package com.example.dyzapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button startButton;
    private ImageView logoImage;
    private TextView welcomeText, fitnessTipText, disclaimerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化WorkoutRecord
        WorkoutRecord workoutRecord = new WorkoutRecord(this);

        // 从云端同步数据
        workoutRecord.syncWithCloud(new WorkoutRecord.SyncCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "数据同步成功", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "数据同步失败：" + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });

        // 绑定控件
        startButton = findViewById(R.id.start_button);
        logoImage = findViewById(R.id.logo_image);
        welcomeText = findViewById(R.id.welcome_text);
        fitnessTipText = findViewById(R.id.fitness_tip_text);
        disclaimerText = findViewById(R.id.disclaimer_text);

        // 设置动画效果
        startAnimations();

        // 按钮点击事件
        startButton.setOnClickListener(v -> navigateToMainFragmentActivity());
    }

    // 启动动画
    private void startAnimations() {
        // 先把所有控件透明度设置为 0
        logoImage.setAlpha(0f);
        welcomeText.setAlpha(0f);
        fitnessTipText.setAlpha(0f);
        disclaimerText.setAlpha(0f);
        startButton.setAlpha(0f);

        // 创建淡入动画
        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoImage, "alpha", 0f, 1f);
        logoFadeIn.setDuration(800);

        ObjectAnimator welcomeFadeIn = ObjectAnimator.ofFloat(welcomeText, "alpha", 0f, 1f);
        welcomeFadeIn.setDuration(800);

        ObjectAnimator tipFadeIn = ObjectAnimator.ofFloat(fitnessTipText, "alpha", 0f, 1f);
        tipFadeIn.setDuration(800);

        ObjectAnimator disclaimerFadeIn = ObjectAnimator.ofFloat(disclaimerText, "alpha", 0f, 1f);
        disclaimerFadeIn.setDuration(800);

        ObjectAnimator buttonFadeIn = ObjectAnimator.ofFloat(startButton, "alpha", 0f, 1f);
        buttonFadeIn.setDuration(800);

        // 让动画依次播放
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(logoFadeIn, welcomeFadeIn, tipFadeIn, disclaimerFadeIn, buttonFadeIn);
        animatorSet.start();
    }

    // 页面跳转
    private void navigateToMainFragmentActivity() {
        Intent intent = new Intent(MainActivity.this, MainFragmentActivity.class);
        startActivity(intent);
        finish(); // 结束当前Activity
    }
}

