package com.tachyon.bindaas.SoundLists;

import java.util.ArrayList;


public class Sounds_GetSet {

    public String id, sound_name, description, section, thum = "", date_created, fav;
    public String acc_path;
    //public String mp3_path;
}

class Sound_catagory_Get_Set {
    public String catagory;
    ArrayList<Sounds_GetSet> sound_list = new ArrayList<>();
}
