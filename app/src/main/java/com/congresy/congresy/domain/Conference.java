
package com.congresy.congresy.domain;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Conference {

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
    @SerializedName("organizators")
    @Expose
    private List<String> organizators = null;
    @SerializedName("popularity")
    @Expose
    private Integer popularity;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("speakersNames")
    @Expose
    private String speakersNames;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("theme")
    @Expose
    private String theme;

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

    public List<String> getOrganizators() {
        return organizators;
    }

    public void setOrganizators(List<String> organizators) {
        this.organizators = organizators;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
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
}