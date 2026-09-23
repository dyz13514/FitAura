package com.example.dyzapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MusicPlayActivity extends AppCompatActivity {

    private TextView tvMusicName;
    private ImageView ivMusicPic;
    private ImageButton ibPlay, ibPause;
    private String[] musicNameList = {"APT", "告白气球", "海阔天空"};
    private int[] musicPicId = {R.drawable.apt, R.drawable.gbqq, R.drawable.hktk};
    private int[] musicFileId = {R.raw.apt, R.raw.gbqq, R.raw.hktk};
    private int id = 0;
    private Intent intentActivity, intentService;

    private SeekBar seekBar;  // 在类中声明 seekBar

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.example.dyzapplication.UPDATE_PROGRESS")) {
                int progress = intent.getIntExtra("progress", 0);
                int duration = intent.getIntExtra("duration", 0);
                seekBar.setProgress(progress);
                seekBar.setMax(duration);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_play);

        tvMusicName = findViewById(R.id.musicPlay_musicName);
        ivMusicPic = findViewById(R.id.musicPlay_pic);
        ibPlay = findViewById(R.id.musicPlay_play);
        ibPause = findViewById(R.id.musicPlay_pause);

        intentActivity = getIntent();
        String musicName = intentActivity.getStringExtra("musicName");
        tvMusicName.setText(musicName);

        for (int i = 0; i < musicNameList.length; i++) {
            if (musicNameList[i].equals(musicName)) {
                id = i;
                break;
            }
        }
        ivMusicPic.setImageResource(musicPicId[id]);

        intentService = new Intent(MusicPlayActivity.this, MusicService.class);
        intentService.putExtra("MusicName", musicNameList[id]);
        intentService.putExtra("MusicFile", musicFileId[id]);

        ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentService.putExtra("Action", "play");
                startService(intentService);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ibPlay.setVisibility(View.INVISIBLE);
                        ibPause.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        ibPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentService.putExtra("Action", "pause");
                startService(intentService);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ibPause.setVisibility(View.INVISIBLE);
                        ibPlay.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        seekBar = findViewById(R.id.musicPlay_seekBar);  // 在这里初始化 seekBar
        seekBar.setMax(100); // 假设最大值为100，实际应根据音乐文件长度设置
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    intentService.putExtra("Action", "seekTo");
                    intentService.putExtra("Position", progress);
                    startService(intentService);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        IntentFilter filter = new IntentFilter("com.example.dyzapplication.UPDATE_PROGRESS");
        registerReceiver(progressReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentService);
    }
}
