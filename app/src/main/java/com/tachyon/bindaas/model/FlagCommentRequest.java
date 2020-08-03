package com.tachyon.bindaas.model;


public class FlagCommentRequest {
    private String comment_id;

    private String user_id;

    public void setComment_id(String comment_id){
        this.comment_id = comment_id;
    }
    public String getComment_id(){
        return this.comment_id;
    }
    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
    public String getUser_id(){
        return this.user_id;
    }

}
