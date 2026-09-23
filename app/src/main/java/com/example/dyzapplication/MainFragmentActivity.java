package com.example.dyzapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainFragmentActivity extends AppCompatActivity {
    private ImageButton BtIndex;
    private ImageButton Bt2;
    private ImageButton Bt3;
    private ImageButton Bt4;
    private Vibrator vibrator; // 用来震动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        // 初始化按钮和震动器
        BtIndex = findViewById(R.id.fragment_b1);
        Bt2 = findViewById(R.id.fragment_b2);
        Bt3 = findViewById(R.id.fragment_b3);
        Bt4 = findViewById(R.id.fragment_b4);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); // 获取震动服务

        // 设置按钮点击事件监听
        BtIndex.setOnClickListener(new MyClickListener());
        Bt2.setOnClickListener(new MyClickListener());
        Bt3.setOnClickListener(new MyClickListener());
        Bt4.setOnClickListener(new MyClickListener());

        // 初始加载 IndexFragment 并设置默认选中状态
        loadFragment(new IndexFragment());
        updateButtonFeedback(R.id.fragment_b1);
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

    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 点击按钮时先震动反馈
            if (vibrator != null && vibrator.hasVibrator()) {
                vibrator.vibrate(50);  // 震动 50 毫秒
            }

            // 根据点击的按钮，加载不同的 Fragment
            if (v.getId() == R.id.fragment_b1) {
                loadFragment(new IndexFragment());
            } else if (v.getId() == R.id.fragment_b2) {
                loadFragment(new SecondFragment());
            } else if (v.getId() == R.id.fragment_b3) {
                loadFragment(new ThirdFragment());
            } else if (v.getId() == R.id.fragment_b4) {
                loadFragment(new FourthFragment());
            }

            // 更新按钮的反馈效果
            updateButtonFeedback(v.getId());
        }
    }

    // 加载 Fragment 的通用方法
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_top, fragment);
        fragmentTransaction.commit();
    }

    // 更新按钮的反馈效果
    private void updateButtonFeedback(int clickedButtonId) {
        // 重置所有按钮的状态
        resetButtonStates();

        // 为被点击的按钮设置选中状态
        if (clickedButtonId == R.id.fragment_b1) {
            BtIndex.setSelected(true);
        } else if (clickedButtonId == R.id.fragment_b2) {
            Bt2.setSelected(true);
        } else if (clickedButtonId == R.id.fragment_b3) {
            Bt3.setSelected(true);
        } else if (clickedButtonId == R.id.fragment_b4) {
            Bt4.setSelected(true);
        }
    }

    // 重置所有按钮的状态
    private void resetButtonStates() {
        BtIndex.setSelected(false);
        Bt2.setSelected(false);
        Bt3.setSelected(false);
        Bt4.setSelected(false);
    }
}