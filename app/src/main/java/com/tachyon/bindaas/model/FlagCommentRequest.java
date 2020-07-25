package com.tachyon.bindaas.model;


public class FlagCommentRequest {
    private String comment_id;

    private String fb_id;

    public void setComment_id(String comment_id){
        this.comment_id = comment_id;
    }
    public String getComment_id(){
        return this.comment_id;
    }
    public void setFb_id(String fb_id){
        this.fb_id = fb_id;
    }
    public String getFb_id(){
        return this.fb_id;
    }

}
