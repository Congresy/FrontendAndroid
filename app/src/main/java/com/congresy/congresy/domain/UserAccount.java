package com.congresy.congresy.domain;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAccount {

    @SerializedName("accountNonExpired")
    @Expose
    private Boolean accountNonExpired;
    @SerializedName("accountNonLocked")
    @Expose
    private Boolean accountNonLocked;
    @SerializedName("authorities")
    @Expose
    private List<Authority> authorities = null;
    @SerializedName("credentialsNonExpired")
    @Expose
    private Boolean credentialsNonExpired;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("username")
    @Expose
    private String username;

    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
