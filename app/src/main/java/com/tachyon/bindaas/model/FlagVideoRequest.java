
package com.tachyon.bindaas.model;


public class FlagVideoRequest {

    private String video_id;

    private String fb_id;

    private String reason;

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_id() {
        return this.video_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getFb_id() {
        return this.fb_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}


