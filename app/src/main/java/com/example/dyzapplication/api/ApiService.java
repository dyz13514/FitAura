package com.example.dyzapplication.api;

import com.example.dyzapplication.database.WorkoutEntity;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiService {
    private static final String BASE_URL = "http://your-aliyun-server.com/api"; // 替换为阿里云服务器地址
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = createClient();
    private static final Gson gson = new Gson();

    private static OkHttpClient createClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public static void uploadWorkout(WorkoutEntity workout, ApiCallback callback) {
        new Thread(() -> {
            try {
                String json = gson.toJson(workout);
                RequestBody body = RequestBody.create(json, JSON);
                Request request = new Request.Builder()
                        .url(BASE_URL + "/workouts")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("请求失败: " + response);
                    String responseData = response.body() != null ? response.body().string() : "";
                    callback.onSuccess(responseData);
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public static void syncFromServer(ApiCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/workouts")
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("请求失败: " + response);
                    String responseData = response.body() != null ? response.body().string() : "";
                    callback.onSuccess(responseData);
                }
            } catch (Exception e) {
                callback.onError(e);
            }
        }).start();
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(Exception e);
    }
} 