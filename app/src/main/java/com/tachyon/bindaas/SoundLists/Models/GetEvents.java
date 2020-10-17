package com.tachyon.bindaas.SoundLists.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetEvents {

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("msg")
    @Expose
    private List<EventsModel> events = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<EventsModel> getEvents() {
        return events;
    }

    public void setEvents(List<EventsModel> msg) {
        this.events = msg;
    }

}