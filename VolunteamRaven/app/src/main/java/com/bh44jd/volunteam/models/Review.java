package com.BH44IT.volunteam.models;

public class Review {

    private String rating;
    private String event;
    private String reviewer;
    private String comment;

    public Review(String rating, String event, String reviewer, String comment) {
        this.rating = rating;
        this.event = event;
        this.reviewer = reviewer;
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
