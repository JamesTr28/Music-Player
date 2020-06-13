package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SongPlaying extends AppCompatActivity {

    public static final String Broadcast_PLAY_NEW_SONG = "com.example.musicplayer.PlayNewSong";

    int songIndex;
    ArrayList<Song> songList;
    ArrayList<Song> favoriteList;
    Storage storage;

    private MediaPlayerService player ;
    boolean serviceBound = false;
    boolean isRepeat = false;

    ImageView album;
    SeekBar positionBar, volumeBar;
    TextView elapsedTimeLabel, remainingTimeLabel, songTitle;
    Button playBtn, repeatBtn, nextBtn, previousBtn, starBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_layout);

        //Receive song's details
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("BUNDLE");
        songIndex = (Integer) bundle.getInt("INDEX");
        songList = (ArrayList<Song>) bundle.getSerializable("LIST");

        //Variables
        album = findViewById(R.id.album);
        songTitle = findViewById(R.id.Title);
        positionBar = findViewById(R.id.positionBar);
        volumeBar = findViewById(R.id.volumeBar);
        elapsedTimeLabel = findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.remainingTimeLabel);
        playBtn = findViewById(R.id.playBtn);
        repeatBtn = findViewById(R.id.repeatBtn);
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

        positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                   player.seekTo(i);
                    positionBar.setProgress(i);
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

        // Thread (Update positionBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (player != null) {
                    try {
                        Message msg = new Message();
                        msg.what = player.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) { System.out.println("Something went wrong.");}
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(player.getDuration() - currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel ;
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
            //Initiate the activity after connect to service
            seekBar();
            songDetail();

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

    //Save data to instanceState
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

    //Unbind service
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
    //Repeat Button
    public void repeatAction(View v) {
        if(isRepeat) {
            isRepeat = false;
            Toast.makeText(getApplicationContext(), "Repeat Mode Deactivated!", Toast.LENGTH_SHORT).show();
            player.IsRepeat = false;
        }
        else {
            isRepeat = true;
            Toast.makeText(getApplicationContext(), "Repeat Mode Activated!", Toast.LENGTH_SHORT).show();
            player.IsRepeat = true;
        }

    }

    //Save favorite song
    public void favoriteAction(View v) {
        boolean isStored = false;
        storage = new Storage(getApplicationContext());
        favoriteList = storage.loadFavoriteSong();
        Song currentSong = player.getActiveSong();
        if(favoriteList == null) {
            favoriteList = new ArrayList<>();
            favoriteList.add(currentSong);
            Toast.makeText(getApplicationContext(), "Saved to Favorite", Toast.LENGTH_SHORT).show();
        } else {
            for(int i = 0; i < favoriteList.size(); i++) {
                if (favoriteList.get(i).getTitle().equals(currentSong.getTitle())) {
                    Toast.makeText(getApplicationContext(), "Song Has been already saved to Favorites", Toast.LENGTH_SHORT).show();
                    isStored = true;
                }
            }
            if(!isStored) {
                favoriteList.add(currentSong);
                Toast.makeText(getApplicationContext(), "Saved to Favorite", Toast.LENGTH_SHORT).show();
            }
        }
        storage.storeFavoriteSong(favoriteList);
    }

    //Update song details
    public void songDetail() {
        songTitle.setText(player.getTitle());
        albumArt();
    }

    //Get album art
    public void albumArt() {
        long SongAlbumID = Long.parseLong(player.getActiveSong().getAlbumID());

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, SongAlbumID);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.album);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(SongPlaying.this.getContentResolver(), albumArtUri);
        }catch(Exception e){e.printStackTrace();}

        //set bitmap for ImageView
        album.setImageBitmap(bitmap);
    }
}