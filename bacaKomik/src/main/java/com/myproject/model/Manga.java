package com.myproject.model;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */




public class Manga {
    private final String id;
    private final String title;
    private final String image;
    private final String description;
    private final String status;
    private final String latestChapter;

    // constructor, getter, setter
    public Manga(String id, String title, String image, String description, String status, String latestChapter) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.status = status;
        this.latestChapter = latestChapter;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getLatestChapter() { return latestChapter; }
}