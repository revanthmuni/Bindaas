package com.tachyon.bindaas.SoundLists.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventsModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("event_name")
    @Expose
    private String eventName;
    @SerializedName("short_description")
    @Expose
    private String shortDescription;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("sound_image")
    @Expose
    private String soundImage;
    @SerializedName("sound_section_id")
    @Expose
    private String soundSectionId;
    @SerializedName("discover_image")
    @Expose
    private String discoverImage;
    @SerializedName("discover_section_id")
    @Expose
    private String discoverSectionId;
    @SerializedName("created")
    @Expose
    private String created;

    public String getSoundSectionId() {
        return soundSectionId;
    }

    public void setSoundSectionId(String soundSectionId) {
        this.soundSectionId = soundSectionId;
    }

    public String getDiscoverSectionId() {
        return discoverSectionId;
    }

    public void setDiscoverSectionId(String discoverSectionId) {
        this.discoverSectionId = discoverSectionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getSoundImage() {
        return soundImage;
    }

    public void setSoundImage(String soundImage) {
        this.soundImage = soundImage;
    }


    public String getDiscoverImage() {
        return discoverImage;
    }

    public void setDiscoverImage(String discoverImage) {
        this.discoverImage = discoverImage;
    }


    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

}
