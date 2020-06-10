package com.example.touristguide.utils;

public class PlaceInfo {
    /*This class is used for getting all the information of each tourist place*/
    private String placeName;
    private int placeImage;
    private double latitude;
    private double longitude;
    private int[] imageList;
    private String about;
    private String timing;
    private String tickets;
    private String nearbyplaces;
    private String howtoreach;
    private String nearbyhotels;

    public PlaceInfo(){}

    public PlaceInfo(int i, String placeName, int placeImage, double latitude, double longitude) {
        this.placeName = placeName;
        this.placeImage = placeImage;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public PlaceInfo(String placeName, int placeImage) {
        this.placeName = placeName;
        this.placeImage = placeImage;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public String getNearbyplaces() {
        return nearbyplaces;
    }

    public void setNearbyplaces(String nearbyplaces) {
        this.nearbyplaces = nearbyplaces;
    }

    public String getHowtoreach() {
        return howtoreach;
    }

    public void setHowtoreach(String howtoreach) {
        this.howtoreach = howtoreach;
    }

    public String getNearbyhotels() {
        return nearbyhotels;
    }

    public void setNearbyhotels(String nearbyhotels) {
        this.nearbyhotels = nearbyhotels;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int[] getImageList() {
        return imageList;
    }

    public void setImageList(int[] imageList) {
        this.imageList = imageList;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getPlaceImage() {
        return placeImage;
    }

    public void setPlaceImage(int placeImage) {
        this.placeImage = placeImage;
    }
}
