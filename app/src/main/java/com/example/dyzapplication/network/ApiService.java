package com.example.dyzapplication.network;

import android.util.Log;
import com.example.dyzapplication.database.WorkoutEntity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ApiService {
    private static final String TAG = "ApiService";
    // 替换成您的阿里云服务器地址
    private static final String BASE_URL = "http://your-aliyun-server.com/api";
    
    // 上传训练记录到服务器
    public static void uploadWorkout(WorkoutEntity workout, ApiCallback callback) {
        new Thread(() -> {
            try {
                // 构建JSON数据
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("date", workout.getDate());
                jsonBody.put("bodyPart", workout.getBodyPart());
                jsonBody.put("intensity", workout.getIntensity());
                jsonBody.put("duration", workout.getDuration());
                jsonBody.put("exercises", workout.getExercises());
                jsonBody.put("customExercises", workout.getCustomExercises());

                // 创建HTTP连接
                URL url = new URL(BASE_URL + "/workouts");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // 发送数据
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // 获取响应
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 读取响应数据
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        callback.onSuccess(response.toString());
                    }
                } else {
                    throw new Exception("服务器返回错误: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "上传失败", e);
                callback.onError(e);
            }
        }).start();
    }

    // 从服务器同步数据
    public static void syncFromServer(ApiCallback callback) {
        new Thread(() -> {
            try {
                // 创建HTTP连接
                URL url = new URL(BASE_URL + "/workouts");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // 获取响应
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 读取响应数据
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        callback.onSuccess(response.toString());
                    }
                } else {
                    throw new Exception("服务器返回错误: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "同步失败", e);
                callback.onError(e);
            }
        }).start();
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }
} 