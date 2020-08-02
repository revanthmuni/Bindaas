package com.tachyon.bindaas.model;

class SignUpRequest {
    private String user_id;

    private String first_name;

    private String last_name;

    private String profile_pic;

    private String gender;

    private String version;

    private String signup_type;

    private String device;

    private String deviceid;

    private String token;

    private String username;

    private String password;

    private String block;

    public void setUser_id(String user_id){
        this.user_id = user_id;
    }
    public String getUser_id(){
        return this.user_id;
    }
    public void setFirst_name(String first_name){
        this.first_name = first_name;
    }
    public String getFirst_name(){
        return this.first_name;
    }
    public void setLast_name(String last_name){
        this.last_name = last_name;
    }
    public String getLast_name(){
        return this.last_name;
    }
    public void setProfile_pic(String profile_pic){
        this.profile_pic = profile_pic;
    }
    public String getProfile_pic(){
        return this.profile_pic;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    public String getGender(){
        return this.gender;
    }
    public void setVersion(String version){
        this.version = version;
    }
    public String getVersion(){
        return this.version;
    }
    public void setSignup_type(String signup_type){
        this.signup_type = signup_type;
    }
    public String getSignup_type(){
        return this.signup_type;
    }
    public void setDevice(String device){
        this.device = device;
    }
    public String getDevice(){
        return this.device;
    }
    public void setDeviceid(String deviceid){
        this.deviceid = deviceid;
    }
    public String getDeviceid(){
        return this.deviceid;
    }
    public void setToken(String token){
        this.token = token;
    }
    public String getToken(){
        return this.token;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return this.password;
    }
    public void setBlock(String block){
        this.block = block;
    }
    public String getBlock(){
        return this.block;
    }
}
