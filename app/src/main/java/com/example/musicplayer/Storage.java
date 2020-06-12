package com.example.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Storage {

    private final String STORAGE = " com.example.musicplayer.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    public Storage(Context context) {
        this.context = context;
    }

    public void storeSong(ArrayList<Song> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("songArrayList", json);
        editor.apply();
    }

    public void storeFavoriteSong(ArrayList<Song> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("favoriteSong", json);
        editor.apply();
    }

    public ArrayList<Song> loadSong() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("songArrayList", null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public ArrayList<Song> loadFavoriteSong() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("favoriteSong", null);
        Type type = new TypeToken<ArrayList<Song>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void storeSongIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("songIndex", index);
        editor.apply();
    }

    public int loadSongIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getInt("songIndex", -1);//return -1 if no data found
    }


    public void clearCachedSongPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("songIndex");
        editor.remove("songArrayList");
        editor.commit();
    }
}
