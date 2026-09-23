package com.example.dyzapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Handler;

public class MusicService extends Service {
    private MediaPlayer mp;
    private Handler progressHandler = new Handler();
    private final String TAG = MusicService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String musicName = intent.getStringExtra("MusicName");
        int musicFileId = intent.getIntExtra("MusicFile", 0);
        String action = intent.getStringExtra("Action");

        if ("play".equals(action)) {
            if (mp == null) {
                mp = MediaPlayer.create(this, musicFileId);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        sendBroadcast(new Intent("com.example.dyzapplication.MUSIC_COMPLETED"));
                    }
                });
            }
            if (mp != null && !mp.isPlaying()) {
                mp.start();
                updateMusicProgress();
            }
        } else if ("pause".equals(action)) {
            if (mp != null && mp.isPlaying()) {
                mp.pause();
                progressHandler.removeCallbacksAndMessages(null);
            }
        } else if ("stop".equals(action)) {
            if (mp != null) {
                mp.stop();
                mp.reset();
                progressHandler.removeCallbacksAndMessages(null);
            }
        } else if ("seekTo".equals(action)) {
            int position = intent.getIntExtra("Position", 0);
            if (mp != null) {
                mp.seekTo(position);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateMusicProgress() {
        progressHandler.postDelayed(progressRunnable, 1000);
    }

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mp != null && mp.isPlaying()) {
                int currentPosition = mp.getCurrentPosition();
                Intent progressIntent = new Intent("com.example.dyzapplication.UPDATE_PROGRESS");
                progressIntent.putExtra("progress", currentPosition);
                progressIntent.putExtra("duration", mp.getDuration());
                sendBroadcast(progressIntent);
                progressHandler.postDelayed(this, 1000); // Update progress every second
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.release();
            mp = null;
        }
        progressHandler.removeCallbacksAndMessages(null);
    }
}