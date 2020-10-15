package com.tachyon.bindaas.Discover.Models;

import com.tachyon.bindaas.Home.Home_Get_Set;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String id;
    private String section_name;
    private String created;
    ArrayList<Home_Get_Set> list = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public ArrayList<Home_Get_Set> getList() {
        return list;
    }

    public void setList(ArrayList<Home_Get_Set> list) {
        this.list = list;
    }
}
