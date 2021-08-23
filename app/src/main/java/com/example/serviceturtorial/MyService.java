package com.example.serviceturtorial;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Date;

import static com.example.serviceturtorial.MyApplication.CHANNEL_ID;

public class MyService extends Service {

    private MediaPlayer mediaPlayer;
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_START = 4;

    private boolean isPlaying;
    private Song mSong;

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
                mSong = song;
                startMusic(song);
                sendNotificationMedia(song);

            }

        }
        int actionMusic = intent.getIntExtra("action_music_service",0);
        handleMusic(actionMusic);
        return START_NOT_STICKY;
    }
    private void handleMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;
        }
    }

    private void pauseMusic(){
        if (mediaPlayer != null && isPlaying){
            mediaPlayer.pause();
            isPlaying = false;
            sendNotificationMedia(mSong);
            sendActionToActivity(ACTION_PAUSE);
        }
    }
    private void resumeMusic(){
        if (mediaPlayer != null && !isPlaying){
            mediaPlayer.start();
            isPlaying = true;
            sendNotificationMedia(mSong);
            sendActionToActivity(ACTION_RESUME);

        }
    }

    private void startMusic(Song song) {
        if (mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(),song.getResource());
        }
        mediaPlayer.start();
        isPlaying = true;
        sendActionToActivity(ACTION_START);
    }

//    private void sendNotification(@NonNull Song song) {
//        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notification);
//        remoteViews.setTextViewText(R.id.tv_title_song,song.getTitle());
//        remoteViews.setTextViewText(R.id.tv_singer_song,song.getSinger());
//        remoteViews.setImageViewResource(R.id.img_song,song.getImage());
//        remoteViews.setImageViewResource(R.id.img_play_or_pause,R.drawable.ic_baseline_play_arrow_24);
//
//        if (isPlaying){
//            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause,getPendingIntent(this,ACTION_PAUSE));
//            remoteViews.setImageViewResource(R.id.img_play_or_pause,R.drawable.ic_baseline_pause_circle_24);
//        }else {
//            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause,getPendingIntent(this,ACTION_RESUME));
//            remoteViews.setImageViewResource(R.id.img_play_or_pause,R.drawable.ic_baseline_play_arrow_24);
//        }
//
//        remoteViews.setOnClickPendingIntent(R.id.img_clear,getPendingIntent(this,ACTION_CLEAR));
//
//
//        Intent intent = new Intent(this,MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification notification = new NotificationCompat.Builder(this,
//                MyApplication.CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_android_black_24dp)
//                .setContentIntent(pendingIntent)
//                .setCustomContentView(remoteViews)
//                .setSound(null)
//                .build();
//
//
//        startForeground(2,notification);
//    }


    private void sendNotificationMedia(@NonNull Song song){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.my_avt);

        MediaSessionCompat sessionCompat = new MediaSessionCompat(this,"tag");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                .setSubText("My Music")
                .setContentTitle(song.getTitle())
                .setContentText(song.getSinger())
                .setLargeIcon(bitmap)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0,1,2)
                    .setMediaSession(sessionCompat.getSessionToken()));

        if (isPlaying){
            builder.addAction(R.drawable.ic_baseline_skip_previous_24,"Previous",null)
                    .addAction(R.drawable.ic_baseline_pause_circle_24,"Pause",getPendingIntent(this,ACTION_PAUSE))
                    .addAction(R.drawable.ic_baseline_skip_next_24,"Next",null);
        }else {
            builder.addAction(R.drawable.ic_baseline_skip_previous_24,"Previous",null)
                    .addAction(R.drawable.ic_baseline_play_arrow_24,"Pause",getPendingIntent(this,ACTION_RESUME))
                    .addAction(R.drawable.ic_baseline_skip_next_24,"Next",null);
        }
        Notification notification = builder.build();
        startForeground(2,notification);
    }
    private PendingIntent getPendingIntent(Context context, int action){
        Intent intent = new Intent(context,MyBroadcastReceiver.class);
        intent.putExtra("action_music",action);


        return PendingIntent.getBroadcast(context.getApplicationContext(),action,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void sendActionToActivity(int action){
        Intent intent = new Intent("send_data_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song",mSong);
        bundle.putBoolean("status_player",isPlaying);
        bundle.putInt("action_music",action);

        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
//    private int getNotificationId(){
//        return (int) new Date().getTime();
//    }
}
