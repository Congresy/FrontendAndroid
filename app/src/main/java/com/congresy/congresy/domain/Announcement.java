package com.congresy.congresy.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Announcement {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("idConference")
    @Expose
    private String idConference;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdConference() {
        return idConference;
    }

    public void setIdConference(String idConference) {
        this.idConference = idConference;
    }
}