package com.example.doan_music.offline.model;

import java.util.Date;

public class SongOffline {
    private int id;
    private int SongID;
    private String SongName;
    private byte[] SongImage;
    private String ArtistName;
    private String LinkSong;
    private  String LinkLrc;
    private Long download_date ;

    public SongOffline() {
    }

    public SongOffline(int songID, String linkLrc, String linkSong, String artistName, byte[] songImage, String songName) {
        SongID = songID;
        LinkLrc = linkLrc;
        LinkSong = linkSong;
        ArtistName = artistName;
        SongImage = songImage;
        SongName = songName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSongID() {
        return SongID;
    }

    public void setSongID(int songID) {
        SongID = songID;
    }

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public byte[] getSongImage() {
        return SongImage;
    }

    public void setSongImage(byte[] songImage) {
        SongImage = songImage;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public void setArtistName(String artistName) {
        ArtistName = artistName;
    }

    public String getLinkSong() {
        return LinkSong;
    }

    public void setLinkSong(String linkSong) {
        LinkSong = linkSong;
    }

    public String getLinkLrc() {
        return LinkLrc;
    }

    public void setLinkLrc(String linkLrc) {
        LinkLrc = linkLrc;
    }

    public Long getDownload_date() {
        return download_date;
    }

    public void setDownload_date(Long download_date) {
        this.download_date = download_date;
    }
}
