package com.congresy.congresy.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("receiverId")
    @Expose
    private String receiverId;
    @SerializedName("senderId")
    @Expose
    private String senderId;
    @SerializedName("sentMoment")
    @Expose
    private String sentMoment;
    @SerializedName("subject")
    @Expose
    private String subject;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSentMoment() {
        return sentMoment;
    }

    public void setSentMoment(String sentMoment) {
        this.sentMoment = sentMoment;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

}