package com.tachyon.bindaas.SoundLists.Models;

public class SoundCategoryModel {
    private String id;
    private String section_name;

    public SoundCategoryModel(String id, String section_name) {
        this.id = id;
        this.section_name = section_name;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
