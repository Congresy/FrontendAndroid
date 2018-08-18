package com.congresy.congresy.domain;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("allowedParticipants")
    @Expose
    private Integer allowedParticipants;
    @SerializedName("seatsLeft")
    @Expose
    private Integer seatsLeft;
    @SerializedName("conference")
    @Expose
    private String conference;
    @SerializedName("end")
    @Expose
    private String end;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("participants")
    @Expose
    private List<String> participants = null;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("requirements")
    @Expose
    private String requirements;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("speakers")
    @Expose
    private List<String> speakers = null;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("type")
    @Expose
    private String type;

    public Integer getSeatsLeft() {
        return seatsLeft;
    }

    public void setSeatsLeft(Integer seatsLeft) {
        this.seatsLeft = seatsLeft;
    }

    public Integer getAllowedParticipants() {
        return allowedParticipants;
    }

    public void setAllowedParticipants(Integer allowedParticipants) {
        this.allowedParticipants = allowedParticipants;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<String> speakers) {
        this.speakers = speakers;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.name;
    }
}