package com.congresy.congresy.domain;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Actor {

    @SerializedName("banned")
    @Expose
    private Boolean banned;
    @SerializedName("comments")
    @Expose
    private List<String> comments = null;
    @SerializedName("conferences")
    @Expose
    private List<String> conferences = null;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("events")
    @Expose
    private List<String> events = null;
    @SerializedName("folders")
    @Expose
    private List<String> folders = null;
    @SerializedName("following")
    @Expose
    private List<String> following = null;
    @SerializedName("friends")
    @Expose
    private List<String> friends = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("interests")
    @Expose
    private List<String> interests = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("nick")
    @Expose
    private String nick;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("place")
    @Expose
    private String place;
    @SerializedName("private_")
    @Expose
    private Boolean _private;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("socialNetworks")
    @Expose
    private List<String> socialNetworks = null;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("userAccount_")
    @Expose
    private String userAccount;

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getConferences() {
        return conferences;
    }

    public void setConferences(List<String> conferences) {
        this.conferences = conferences;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public List<String> getFolders() {
        return folders;
    }

    public void setFolders(List<String> folders) {
        this.folders = folders;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Boolean getPrivate() {
        return _private;
    }

    public void setPrivate(Boolean _private) {
        this._private = _private;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getSocialNetworks() {
        return socialNetworks;
    }

    public void setSocialNetworks(List<String> socialNetworks) {
        this.socialNetworks = socialNetworks;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

}