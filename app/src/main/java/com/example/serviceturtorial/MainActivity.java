package com.example.serviceturtorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.serviceturtorial.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);

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
}