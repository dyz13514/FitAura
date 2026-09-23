package com.example.dyzapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FourthFragment extends Fragment {

    private TextView userInfor, userDetailInfo;
    private TextView tvTotalWorkouts, tvTotalDuration, tvAvgIntensity;
    private Button fillInfoButton, editNicknameButton;
    private ImageView userAvatar;
    private int LogRequestCode = 2, LogResultCode = 3;
    private int FillInfoRequestCode = 4;
    private int PICK_IMAGE_REQUEST = 5;
    private WorkoutRecord workoutRecord;
    private String currentUsername = "";

    public FourthFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fourth, container, false);
        
        // 初始化控件
        userInfor = v.findViewById(R.id.UserFragment);
        userDetailInfo = v.findViewById(R.id.user_detail_info);
        fillInfoButton = v.findViewById(R.id.fill_info_button);
        editNicknameButton = v.findViewById(R.id.edit_nickname_button);
        userAvatar = v.findViewById(R.id.user_avatar);
        tvTotalWorkouts = v.findViewById(R.id.tv_total_workouts);
        tvTotalDuration = v.findViewById(R.id.tv_total_duration);
        tvAvgIntensity = v.findViewById(R.id.tv_avg_intensity);
        
        // 初始化训练记录管理器
        workoutRecord = new WorkoutRecord(getActivity());

        // 读取用户信息
        SharedPreferences spUserInfor = getActivity().getSharedPreferences("UserInfor", Context.MODE_PRIVATE);
        currentUsername = spUserInfor.getString("logUser", "");
        if (!currentUsername.equals("")) {
            userInfor.setText(currentUsername);
        }

        // 读取并显示用户详细信息
        loadUserDetailInfo();
        
        // 加载用户头像
        loadUserAvatar();

        // 用户登录点击事件
        userInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LogInActivity.class);
                startActivityForResult(intent, LogRequestCode);
            }
        });

        // 填写信息按钮点击事件
        fillInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IndexActivity.class);
                startActivityForResult(intent, FillInfoRequestCode);
            }
        });
        
        // 编辑昵称按钮点击事件
        editNicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNicknameDialog();
            }
        });
        
        // 头像点击事件
        userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        
        // 更新训练统计数据
        updateWorkoutStatistics();

        return v;
    }
    
    /**
     * 显示修改昵称对话框
     */
    private void showNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("修改昵称");
        
        // 设置布局
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_change_nickname, null);
        builder.setView(customLayout);
        
        final TextView editNickname = customLayout.findViewById(R.id.edit_nickname);
        
        // 设置当前昵称
        editNickname.setText(currentUsername);
        
        // 设置按钮
        builder.setPositiveButton("保存", (dialog, which) -> {
            String newNickname = editNickname.getText().toString().trim();
            if (!newNickname.isEmpty()) {
                updateNickname(newNickname);
            }
        });
        
        builder.setNegativeButton("取消", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    
    /**
     * 更新昵称
     */
    private void updateNickname(String newNickname) {
        SharedPreferences spUserInfor = getActivity().getSharedPreferences("UserInfor", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spUserInfor.edit();
        editor.putString("logUser", newNickname);
        editor.apply();
        
        currentUsername = newNickname;
        userInfor.setText(newNickname);
        
        Toast.makeText(getActivity(), "昵称已更新", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 选择图片
     */
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择头像"), PICK_IMAGE_REQUEST);
    }
    
    /**
     * 加载用户头像
     */
    private void loadUserAvatar() {
        if (getActivity() == null) return;
        
        // 检查是否有保存的头像
        File avatarFile = new File(getActivity().getFilesDir(), currentUsername + "_avatar.jpg");
        if (avatarFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(avatarFile);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                userAvatar.setImageBitmap(bitmap);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
                // 如果读取失败，设置默认头像
                userAvatar.setImageResource(R.drawable.default_avatar);
            }
        } else {
            // 如果没有保存的头像，设置默认头像
            userAvatar.setImageResource(R.drawable.default_avatar);
        }
    }
    
    /**
     * 保存头像
     */
    private void saveUserAvatar(Bitmap bitmap) {
        if (getActivity() == null || bitmap == null) return;
        
        try {
            File file = new File(getActivity().getFilesDir(), currentUsername + "_avatar.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "保存头像失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LogRequestCode && resultCode == LogResultCode && data != null) {
            currentUsername = data.getStringExtra("username");
            userInfor.setText(currentUsername);
            
            // 登录后加载对应用户的头像和详细信息
            loadUserAvatar();
            loadUserDetailInfo();
        } else if (requestCode == FillInfoRequestCode && resultCode == 5) {
            // 从IndexActivity返回后，重新加载用户详细信息
            loadUserDetailInfo();
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    userAvatar.setImageBitmap(bitmap);
                    saveUserAvatar(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "加载图片失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 读取用户信息
        SharedPreferences spUserInfor = getActivity().getSharedPreferences("UserInfor", Context.MODE_PRIVATE);
        currentUsername = spUserInfor.getString("logUser", "");
        if (!currentUsername.equals("")) {
            userInfor.setText(currentUsername);
        }
        
        // 读取详细信息
        loadUserDetailInfo();
        
        // 更新训练统计
        updateWorkoutStatistics();
        
        // 加载头像
        loadUserAvatar();
    }
    
    /**
     * 更新训练统计数据
     */
    private void updateWorkoutStatistics() {
        workoutRecord.getAllWorkouts(new WorkoutRecord.DataCallback<List<WorkoutRecord.WorkoutData>>() {
            @Override
            public void onSuccess(List<WorkoutRecord.WorkoutData> workouts) {
                if (getActivity() == null) return; // 检查Fragment是否仍然附加到Activity
                getActivity().runOnUiThread(() -> {
                    // 计算总训练次数
                    int totalWorkouts = workouts.size();
                    tvTotalWorkouts.setText(String.valueOf(totalWorkouts));
                    
                    // 计算总训练时长和总强度
                    int totalDuration = 0;
                    int totalIntensity = 0;
                    
                    for (WorkoutRecord.WorkoutData workout : workouts) {
                        totalDuration += workout.duration;
                        totalIntensity += workout.intensity;
                    }
                    
                    tvTotalDuration.setText(String.valueOf(totalDuration));
                    
                    // 计算平均强度
                    float avgIntensity = 0;
                    if (totalWorkouts > 0) {
                        avgIntensity = (float) totalIntensity / totalWorkouts;
                    }
                    
                    tvAvgIntensity.setText(String.format("%.1f", avgIntensity));
                });
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() == null) return; // 检查Fragment是否仍然附加到Activity
                getActivity().runOnUiThread(() -> 
                    Toast.makeText(getActivity(), "加载训练数据失败: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    
    /**
     * 加载用户详细信息
     */
    private void loadUserDetailInfo() {
        if (getActivity() == null) return;
        SharedPreferences spUserInfor = getActivity().getSharedPreferences("UserInfor", Context.MODE_PRIVATE);
        String userInfoKey = !currentUsername.isEmpty() ? currentUsername + "_index_info" : "index_info";
        String indexInfo = spUserInfor.getString(userInfoKey, "");
        if (!indexInfo.isEmpty()) {
            String formattedInfo = indexInfo.replace("\\n", "\n");
            userDetailInfo.setText(formattedInfo);
        } else {
            userDetailInfo.setText("未填写");
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (workoutRecord != null) {
            workoutRecord.close();
        }
    }
}