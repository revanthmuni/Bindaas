package com.tachyon.bindaas.Discover.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TopUsers {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("msg")
    @Expose
    private List<Users> msg = null;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Users> getMsg() {
        return msg;
    }

    public void setMsg(List<Users> msg) {
        this.msg = msg;
    }
}
