package com.example.doan_music.model;

import java.io.Serializable;

public class Song implements Serializable {
    private int SongID;
    private Integer AlbumID;
    private Integer PlaylistID;
    private String SongName;
    private byte[] SongImage;
    private int ArtistID;
    private String LinkSong;
    private int TypeID;
    private int isFavorite;

    private int view;

    public Song(int songId, String songName, String songImage) {
    }

    public Song(int songID, int albumID, String songName, byte[] songImage, int artistID, String linkSong, int playlistID) {
        SongID = songID;
        AlbumID = albumID;
        SongName = songName;
        SongImage = songImage;
        ArtistID = artistID;
        LinkSong = linkSong;
        this.PlaylistID = playlistID;
    }

    public Song(int songID, Integer albumID, Integer playlistID, String songName, byte[] songImage, int view, int isFavorite) {
        SongID = songID;
        AlbumID = albumID;
        PlaylistID = playlistID;
        SongName = songName;
        SongImage = songImage;
        this.view = view;
        this.isFavorite = isFavorite;
    }

    public Song(int id, String songName, byte[] image, String linkSong) {
        SongID = id;
        SongName = songName;
        SongImage = image;
        LinkSong = linkSong;
    }

    public Song(int songID, String songName, byte[] songImage, String linkSong, int isFavorite) {
        SongID = songID;
        SongName = songName;
        SongImage = songImage;
        LinkSong = linkSong;
        this.isFavorite = isFavorite;
    }

    public Song(int id, String name, Integer playListID, byte[] imgBytes, int view) {
        SongID = id;
        SongName = name;
        PlaylistID = playListID;
        SongImage = imgBytes;
        this.view = view;
    }

    public Song(int songID, String songName, int artistID, byte[] songImage, String linkSong, int isFavorite) {
        SongID = songID;
        SongName = songName;
        SongImage = songImage;
        LinkSong = linkSong;
        ArtistID = artistID;
        this.isFavorite = isFavorite;
    }

    public Song(int songID, String songName, byte[] songImage, int view) {
        SongID = songID;
        SongName = songName;
        SongImage = songImage;
        this.view = view;
    }

    public Song(int id, String name, byte[] img, int AlbumID, int view) {
        SongID = id;
        SongName = name;
        SongImage = img;
        this.AlbumID = AlbumID;
        this.view = view;
    }

    public int getSongID() {
        return SongID;
    }

    public void setSongID(int songID) {
        SongID = songID;
    }

    public int getAlbumID() {
        return AlbumID;
    }

    public void setAlbumID(int albumID) {
        AlbumID = albumID;
    }

    public void setAlbumID(Integer albumID) {
        AlbumID = albumID;
    }

    public int getPlaylistID() {
        return PlaylistID;
    }

    public void setPlaylistID(int playlistID) {
        PlaylistID = playlistID;
    }

    public void setPlaylistID(Integer playlistID) {
        PlaylistID = playlistID;
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

    public int getArtistID() {
        return ArtistID;
    }

    public void setArtistID(int artistID) {
        ArtistID = artistID;
    }

    public String getLinkSong() {
        return LinkSong;
    }

    public void setLinkSong(String linkSong) {
        LinkSong = linkSong;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

}
