
package com.congresy.congresy.domain;

import android.content.Intent;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conference {

    @SerializedName("allowedParticipants")
    @Expose
    private Integer allowedParticipants;
    @SerializedName("seatsLeft")
    @Expose
    private Integer seatsLeft;
    @SerializedName("comments")
    @Expose
    private List<String> comments = null;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("events")
    @Expose
    private List<String> events = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("organizator")
    @Expose
    private String organizator;
    @SerializedName("participants")
    @Expose
    private List<String> participants = null;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("popularity")
    @Expose
    private Integer popularity;
    @SerializedName("price")
    @Expose
    private Double price;
    @SerializedName("speakersNames")
    @Expose
    private String speakersNames;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("theme")
    @Expose
    private String theme;

    public Integer getAllowedParticipants() {
        return allowedParticipants;
    }

    public void setAllowedParticipants(Integer allowedParticipants) {
        this.allowedParticipants = allowedParticipants;
    }

    public Integer getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(Integer seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizator() {
        return organizator;
    }

    public void setOrganizator(String organizator) {
        this.organizator = organizator;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSpeakersNames() {
        return speakersNames;
    }

    public void setSpeakersNames(String speakersNames) {
        this.speakersNames = speakersNames;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    @Override
    public String toString(){
        return this.name;
    }

}