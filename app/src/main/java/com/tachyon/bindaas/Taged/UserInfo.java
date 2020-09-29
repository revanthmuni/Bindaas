package com.tachyon.bindaas.Taged;

public class UserInfo {
     String first_name;
     String user_id;
     String username;
     String last_name,profile_pic,verified;

     public String getUsername() {
          return username;
     }

     public void setUsername(String username) {
          this.username = username;
     }

     public String getFirst_name() {
          return first_name;
     }

     public void setFirst_name(String first_name) {
          this.first_name = first_name;
     }

     public String getUser_id() {
          return user_id;
     }

     public void setUser_id(String user_id) {
          this.user_id = user_id;
     }

     public String getLast_name() {
          return last_name;
     }

     public void setLast_name(String last_name) {
          this.last_name = last_name;
     }

     public String getProfile_pic() {
          return profile_pic;
     }

     public void setProfile_pic(String profile_pic) {
          this.profile_pic = profile_pic;
     }

     public String getVerified() {
          return verified;
     }

     public void setVerified(String verified) {
          this.verified = verified;
     }
}
