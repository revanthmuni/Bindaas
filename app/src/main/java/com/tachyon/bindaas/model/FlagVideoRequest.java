
package com.tachyon.bindaas.model;


public class FlagVideoRequest {

    private String video_id;

    private String user_id;

    private String reason;

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getVideo_id() {
        return this.video_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}


