package com.myproject.model;

public class MangaSummary {
    private String id;
    private String title;
    private String image;
    private String description;
    private String status;
    private String latestChapter;
    private String chapter;
    private String updatedAt;
    
    public MangaSummary() {}
    
    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getLatestChapter() { return latestChapter; }
    public void setLatestChapter(String latestChapter) { this.latestChapter = latestChapter; }
    
    public String getChapter() { return chapter; }
    public void setChapter(String chapter) { this.chapter = chapter; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}