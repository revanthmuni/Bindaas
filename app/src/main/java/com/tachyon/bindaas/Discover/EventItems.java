package com.tachyon.bindaas.Discover;

public class EventItems {
    private String id;
    private String sound_image;
    private String discover_image;
    private String event_name;
    private String short_description;
    private String start_date;
    private String end_date;
    private String active;
    private String created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSound_image() {
        return sound_image;
    }

    public void setSound_image(String sound_image) {
        this.sound_image = sound_image;
    }

    public String getDiscover_image() {
        return discover_image;
    }

    public void setDiscover_image(String discover_image) {
        this.discover_image = discover_image;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
