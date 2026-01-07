package com.myproject.service;

import com.myproject.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MangaService {
    private final String baseUrl;
    private final Gson gson = new Gson();

    public MangaService() {
        this.baseUrl = "https://anime-api-seven-steel.vercel.app/manga/mangakakalot";
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    private String fetchJson(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        try (InputStream is = conn.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = is.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toString(StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }
    }

    public PaginatedResponse<MangaSummary> searchManga(String query, int page) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = baseUrl + "/" + encodedQuery + "?page=" + page;
            String json = fetchJson(url);
            Type type = new TypeToken<PaginatedResponse<MangaSummary>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public MangaInfo getMangaInfo(String mangaId) {
        try {
            String encodedId = URLEncoder.encode(mangaId, StandardCharsets.UTF_8);
            String url = baseUrl + "/info?id=" + encodedId;
            String json = fetchJson(url);
            return gson.fromJson(json, MangaInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<ChapterPage> readChapter(String chapterId) {
        try {
            String encodedId = URLEncoder.encode(chapterId, StandardCharsets.UTF_8);
            String url = baseUrl + "/read?chapterId=" + encodedId;
            String json = fetchJson(url);
            Type type = new TypeToken<List<ChapterPage>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public PaginatedResponse<MangaSummary> getLatestManga(int page) {
        try {
            String url = baseUrl + "/latestmanga?page=" + page;
            String json = fetchJson(url);
            Type type = new TypeToken<PaginatedResponse<MangaSummary>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public PaginatedResponse<MangaSummary> getMangaByGenre(String genre, int page) {
        try {
            String encodedGenre = URLEncoder.encode(genre, StandardCharsets.UTF_8);
            String url = baseUrl + "/bygenre?genre=" + encodedGenre + "&page=" + page;
            String json = fetchJson(url);
            Type type = new TypeToken<PaginatedResponse<MangaSummary>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public List<MangaSummary> getSuggestions(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = baseUrl + "/suggestions?query=" + encodedQuery;
            String json = fetchJson(url);
            Type type = new TypeToken<List<MangaSummary>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] getImage(String imageUrl) {
        try {
            byte[] directImage = getImageDirect(imageUrl);
            if (directImage != null && directImage.length > 0) {
                return directImage;
            }
            return getImageViaProxy(imageUrl);
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] getImageDirect(String imageUrl) {
        try {
            URL u = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setRequestProperty("Accept", "image/*");
            conn.setRequestProperty("Referer", "https://www.mangakakalot.gg/");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            String contentType = conn.getContentType();
            if (responseCode == HttpURLConnection.HTTP_OK && contentType != null && contentType.startsWith("image/")) {
                try (InputStream is = conn.getInputStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    return baos.toByteArray();
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private byte[] getImageViaProxy(String imageUrl) {
        try {
            String encodedUrl = URLEncoder.encode(imageUrl, StandardCharsets.UTF_8);
            String url = baseUrl + "/proxy?url=" + encodedUrl;

            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = conn.getInputStream();
                     ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[8192];
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, read);
                    }
                    byte[] responseData = baos.toByteArray();
                    String contentType = conn.getContentType();
                    if (contentType != null && contentType.contains("application/json")) {
                        return handleJsonResponse(responseData);
                    }
                    return responseData;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private byte[] handleJsonResponse(byte[] jsonData) {
        try {
            String jsonStr = new String(jsonData, StandardCharsets.UTF_8);
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
            String[] possibleFields = {"url", "imageUrl", "image", "src", "data"};
            for (String field : possibleFields) {
                if (json.has(field) && json.get(field).isJsonPrimitive()) {
                    String imageUrl = json.get(field).getAsString();
                    return getImageDirect(imageUrl);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
