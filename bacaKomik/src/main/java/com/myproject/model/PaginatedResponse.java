/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myproject.model;


import java.util.List;

public class PaginatedResponse<T> {
    private int currentPage;
    private boolean hasNextPage;
    private List<T> results;

    public PaginatedResponse() {}

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public boolean isHasNextPage() { return hasNextPage; }
    public void setHasNextPage(boolean hasNextPage) { this.hasNextPage = hasNextPage; }

    public List<T> getResults() { return results; }
    public void setResults(List<T> results) { this.results = results; }
}

