package com.example.musicplayer;

import java.io.Serializable;

public class Song implements Serializable {
    //Declare components of each songs
    private String data;
    private String title;
    private String album;
    private String artist;
    private String albumID;

    public Song(String data, String title, String album, String artist, String albumID) {
        this.data = data;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.albumID = albumID;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }
}
