package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SongPlaying extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_SONG = "com.example.musicplayer.PlayNewSong";

    Song song;
    int songIndex;
    ArrayList<Song> songList;
    private MediaPlayerService player ;
    boolean serviceBound = false;


    SeekBar positionBar, volumeBar;
    TextView elapsedTimeLabel, remainingTimeLabel;
    Button playBtn, shuffleBtn, nextBtn, previousBtn, starBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_layout);

        //Receive song's details
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BUNDLE");
        song = (Song) bundle.getSerializable("SONG");
        songIndex = (Integer) bundle.getInt("INDEX");
        songList = (ArrayList<Song>) bundle.getSerializable("LIST");

        //Variables
        positionBar = findViewById(R.id.positionBar);
        volumeBar = findViewById(R.id.volumeBar);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        playBtn = findViewById(R.id.playBtn);
        shuffleBtn = findViewById(R.id.shuffleBtn);
        nextBtn = findViewById(R.id.nextBtn);
        previousBtn = findViewById(R.id.previousBtn);
        starBtn = findViewById(R.id.starBtn);

        //play the song
        playSong(songIndex);
    }

    //indicate positionBar and volumeBar
    public void seekBar() {

        //position Bar
        positionBar.setMax(player.getDuration());
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if(player!=null && player.isPlaying()){
                    positionBar.setProgress(player.getCurrentPosition());
                }
            }
        }, 0, 10);

        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {

                   player.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Volume Bar
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volumeNum = progress / 100f;
                        player.setVolume(volumeNum, volumeNum);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

    }

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
            seekBar();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    //Play song
    private void playSong(int songIndex) {
        //Check is service is active
        if (!serviceBound) {
            //Store Serializable songList to SharedPreferences
            Storage storage = new Storage(getApplicationContext());
            storage.storeSong(songList);
            storage.storeSongIndex(songIndex);

            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Store the new songIndex to SharedPreferences
            Storage storage = new Storage(getApplicationContext());
            storage.storeSongIndex(songIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_SONG);
            sendBroadcast(broadcastIntent);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", serviceBound);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    //Play and Pause Button
    public void playPauseAction(View view) {
        if(player.isPlaying()) {
            player.transportControls.pause();
            playBtn.setBackgroundResource(R.drawable.play);
        } else {
            player.transportControls.play();
            playBtn.setBackgroundResource(R.drawable.stop);
        }
    }

    //Next Song Button
    public void nextAction(View view) {
        player.transportControls.skipToNext();
        playBtn.setBackgroundResource(R.drawable.stop);

    }
    //Previous Song Button
    public void previousAction(View view) {
        player.transportControls.skipToPrevious();
        playBtn.setBackgroundResource(R.drawable.stop);

    }
}