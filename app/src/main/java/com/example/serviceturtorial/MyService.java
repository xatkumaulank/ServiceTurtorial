package com.example.serviceturtorial;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Date;

public class MyService extends Service {

    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("message","Create Service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String dataIntent = intent.getStringExtra("data_intent");
        Bundle bundle = intent.getExtras();
        if (bundle != null){
            Song song = (Song) bundle.get("object_song");

            if (song != null){
                startMusic(song);
                sendNotification(song);
            }

        }
        return START_NOT_STICKY;
    }

    private void startMusic(Song song) {
        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),song.getResource());
        }
        mediaPlayer.start();
    }

    private void sendNotification(@NonNull Song song) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.tv_title_song,song.getTitle());
        remoteViews.setTextViewText(R.id.tv_singer_song,song.getSinger());
        remoteViews.setImageViewResource(R.id.img_song,song.getImage());
        remoteViews.setImageViewResource(R.id.img_play_or_pause,R.drawable.ic_baseline_play_arrow_24);

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this,
                MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();


        startForeground(2,notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
//    private int getNotificationId(){
//        return (int) new Date().getTime();
//    }
}
