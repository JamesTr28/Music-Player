package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SongPlaying extends AppCompatActivity {

    Song song;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_layout);

        //Receive song's details
        Intent intent = getIntent();
        song = (Song) intent.getSerializableExtra("song");
    }
}