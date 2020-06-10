package com.example.touristguide.utils;

public class Rating {
    private String id;
    private String comment;
    private String ratestar;


    public Rating() {
        //this constructor is required
    }

    public Rating(String id, String comment, String ratestar) {
        this.id = id;
        this.comment = comment;
        this.ratestar = ratestar;
    }

    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public String getRatestar() {
        return ratestar;
    }
}
