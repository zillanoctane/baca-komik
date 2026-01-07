package com.myproject.controller;

import com.myproject.model.*;
import com.myproject.service.MangaService;
import java.util.List;

public class MangaController {
    private final MangaService mangaService;

    public MangaController() {
        this.mangaService = new MangaService();
    }

    public PaginatedResponse<MangaSummary> search(String query, int page) {
        validateQuery(query);
        page = validatePage(page);
        
        PaginatedResponse<MangaSummary> result = mangaService.searchManga(query, page);
        if (result == null) {
            throw new RuntimeException("Failed to search manga");
        }
        return result;
    }

    public MangaInfo getMangaInfo(String mangaId) {
        validateId(mangaId, "Manga ID");
        
        MangaInfo mangaInfo = mangaService.getMangaInfo(mangaId);
        if (mangaInfo == null) {
            throw new RuntimeException("Manga not found or failed to fetch info");
        }
        return mangaInfo;
    }

    public List<ChapterPage> readChapter(String chapterId) {
        validateId(chapterId, "Chapter ID");
        
        List<ChapterPage> pages = mangaService.readChapter(chapterId);
        if (pages == null || pages.isEmpty()) {
            throw new RuntimeException("Chapter not found or failed to load");
        }
        return pages;
    }

    public PaginatedResponse<MangaSummary> getLatestManga(int page) {
        page = validatePage(page);
        
        PaginatedResponse<MangaSummary> result = mangaService.getLatestManga(page);
        if (result == null) {
            throw new RuntimeException("Failed to get latest manga");
        }
        return result;
    }

    public PaginatedResponse<MangaSummary> getMangaByGenre(String genre, int page) {
        validateQuery(genre, "Genre");
        page = validatePage(page);
        
        PaginatedResponse<MangaSummary> result = mangaService.getMangaByGenre(genre, page);
        if (result == null) {
            throw new RuntimeException("Failed to get manga by genre");
        }
        return result;
    }

    public List<MangaSummary> getSuggestions(String query) {
        validateQuery(query);
        
        List<MangaSummary> suggestions = mangaService.getSuggestions(query);
        if (suggestions == null) {
            throw new RuntimeException("Failed to get suggestions");
        }
        return suggestions;
    }

    public byte[] getImage(String imageUrl) {
        validateQuery(imageUrl, "Image URL");
        
        byte[] imageData = mangaService.getImage(imageUrl);
        if (imageData == null || imageData.length == 0) {
            throw new RuntimeException("Failed to load image");
        }
        return imageData;
    }

    public List<ChapterInfo> getChaptersByMangaId(String mangaId) {
        MangaInfo mangaInfo = getMangaInfo(mangaId);
        if (mangaInfo.getChapters() == null) {
            throw new RuntimeException("No chapters found for this manga");
        }
        return mangaInfo.getChapters();
    }

    private void validateQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }
    }
    
    private void validateQuery(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }
    
    private void validateId(String id, String idType) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(idType + " cannot be empty");
        }
    }
    
    private int validatePage(int page) {
        return page < 1 ? 1 : page;
    }
}