package com.congresy.congresy.domain;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("authorName")
    @Expose
    private String authorName;
    @SerializedName("authorId")
    @Expose
    private String authorId;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("draft")
    @Expose
    private Boolean draft;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("comments")
    @Expose
    private List<String> comments = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("posted")
    @Expose
    private String posted;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("votes")
    @Expose
    private Integer votes;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Boolean getDraft() {
        return draft;
    }

    public void setVotes(Boolean draft) {
        this.draft = draft;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}