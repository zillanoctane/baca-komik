package com.myproject.model;

public class ChapterInfo {

    private String id;
    private String title;
    private String views;
    private String releaseDate;

    public ChapterInfo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getReleasedDate() {
        return releaseDate;
    }

    public void setReleasedDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
