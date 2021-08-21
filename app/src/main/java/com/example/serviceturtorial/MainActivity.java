package com.example.serviceturtorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.example.serviceturtorial.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private Song mSong;
    private boolean isPlaying;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null){
                return;
            }
            mSong = (Song) bundle.get("object_song");
            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");

            handleLayoutMusic(actionMusic);
        }
    };

    private void handleLayoutMusic(int actionMusic) {
        switch (actionMusic){
            case MyService.ACTION_START:
                binding.layoutBottom.setVisibility(View.VISIBLE);
                showInfoSong();
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_PAUSE:
            case MyService.ACTION_RESUME:
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                binding.layoutBottom.setVisibility(View.GONE);
                break;
        }
    }
    private void showInfoSong(){
        if (mSong == null){
            return;
        }
        binding.imgSong.setImageResource(mSong.getImage());
        binding.tvTitleSong.setText(mSong.getTitle());
        binding.tvSingerSong.setText(mSong.getSinger());

        binding.imgPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying){
                    sendActionToService(MyService.ACTION_PAUSE);
                }else {
                    sendActionToService(MyService.ACTION_RESUME);
                }
            }
        });
        binding.imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActionToService(MyService.ACTION_CLEAR);
            }
        });
    }

    private void setStatusButtonPlayOrPause(){
        if (isPlaying){
            binding.imgPlayOrPause.setImageResource(R.drawable.ic_baseline_pause_circle_24);
        }else {
            binding.imgPlayOrPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }


    private void sendActionToService(int action){
        Intent intent = new Intent(this,MyService.class);
        intent.putExtra("action_music_service",action);

        startService(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter("send_data_to_activity"));

        binding.btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStartService();
            }
        });
        binding.btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStopService();
            }
        });
    }

    private void clickStopService() {

        Intent intent = new Intent(this, Service.class);

        stopService(intent);
    }

    private void clickStartService() {
        Song song = new Song("Hoavenger","Bui Huy Hoa",
                R.drawable.ic_baseline_play_arrow_24,R.raw.music);
        Intent intent = new Intent(this, MyService.class);
        //intent.putExtra("data_intent",binding.edtDataIntent.getText().toString().trim());

        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song",song);
        intent.putExtras(bundle);

        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }
}