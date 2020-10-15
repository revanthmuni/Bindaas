package com.tachyon.bindaas.Discover.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Users {
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("verified")
    @Expose
    private String verified;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("anyone_can_message")
    @Expose
    private String anyoneCanMessage;
    @SerializedName("auto_scroll")
    @Expose
    private String autoScroll;
    @SerializedName("theme")
    @Expose
    private String theme;
    @SerializedName("fb_link")
    @Expose
    private String fbLink;
    @SerializedName("insta_link")
    @Expose
    private String instaLink;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("show_video_preview")
    @Expose
    private String showVideoPreview;
    @SerializedName("star_meter")
    @Expose
    private String starMeter;
    @SerializedName("totalLikes")
    @Expose
    private String totalLikes;
    @SerializedName("totalViews")
    @Expose
    private String totalViews;
    @SerializedName("totalVideoUploads")
    @Expose
    private String totalVideoUploads;
    @SerializedName("totalUserScore")
    @Expose
    private String totalUserScore;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAnyoneCanMessage() {
        return anyoneCanMessage;
    }

    public void setAnyoneCanMessage(String anyoneCanMessage) {
        this.anyoneCanMessage = anyoneCanMessage;
    }

    public String getAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(String autoScroll) {
        this.autoScroll = autoScroll;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getFbLink() {
        return fbLink;
    }

    public void setFbLink(String fbLink) {
        this.fbLink = fbLink;
    }

    public String getInstaLink() {
        return instaLink;
    }

    public void setInstaLink(String instaLink) {
        this.instaLink = instaLink;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getShowVideoPreview() {
        return showVideoPreview;
    }

    public void setShowVideoPreview(String showVideoPreview) {
        this.showVideoPreview = showVideoPreview;
    }

    public String getStarMeter() {
        return starMeter;
    }

    public void setStarMeter(String starMeter) {
        this.starMeter = starMeter;
    }

    public String getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(String totalLikes) {
        this.totalLikes = totalLikes;
    }

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getTotalVideoUploads() {
        return totalVideoUploads;
    }

    public void setTotalVideoUploads(String totalVideoUploads) {
        this.totalVideoUploads = totalVideoUploads;
    }

    public String getTotalUserScore() {
        return totalUserScore;
    }

    public void setTotalUserScore(String totalUserScore) {
        this.totalUserScore = totalUserScore;
    }
}
