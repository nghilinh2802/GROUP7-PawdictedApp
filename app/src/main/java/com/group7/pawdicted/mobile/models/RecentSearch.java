package com.group7.pawdicted.mobile.models;

public class RecentSearch {
    private String searchTerm;
    private long timestamp;

    public RecentSearch() {}

    public RecentSearch(String searchTerm, long timestamp) {
        this.searchTerm = searchTerm;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

}
