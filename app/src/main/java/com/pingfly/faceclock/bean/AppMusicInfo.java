package com.pingfly.faceclock.bean;

/**
 * 定义手机本地音乐文件类
 */
public class AppMusicInfo {
    private long id;
    private String artist;    // 创作者
    private String title;     // 曲名
    private String path;      // 路径
    private long duration;    // 时长
    private long size;       // 大小
    private String url;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public  String getPath() {
        return path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}


