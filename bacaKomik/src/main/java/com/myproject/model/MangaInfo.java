/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myproject.model;


import java.util.List;


public class MangaInfo {
    private String id;
    private String title;
    private List<String> altTitles;
    private String description;
    private List<String> genres;
    private String status;
    private String image;
    private List<String> authors;
    private List<ChapterInfo> chapters;

    public MangaInfo() {}

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getAltTitles() { return altTitles; }
    public void setAltTitles(List<String> altTitles) { this.altTitles = altTitles; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getGenres() { return genres; }
    public void setGenres(List<String> genres) { this.genres = genres; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    public List<ChapterInfo> getChapters() { return chapters; }
    public void setChapters(List<ChapterInfo> chapters) { this.chapters = chapters; }
}
