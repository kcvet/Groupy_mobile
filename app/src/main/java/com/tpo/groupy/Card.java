package com.tpo.groupy;

public class Card {
    private String name, description, place_to_visit, place_to_stay, photo;
    private int numOfSongs;
    private int thumbnail;
    // State of the item
    private boolean expanded;


    public Card(String name, int numOfSongs, int thumbnail, String description, String place_to_visit, String place_to_stay, String photo ) {
        this.name = name;
        this.numOfSongs = numOfSongs;
        this.thumbnail = thumbnail;
        this.description = description;
        this.place_to_stay = place_to_stay;
        this.place_to_visit = place_to_visit;
        this.photo = photo;

    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPlace_to_stay() {
        return place_to_stay;
    }

    public String getPlace_to_visit() {
        return place_to_visit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public String getDescription() {
        return description;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }
}