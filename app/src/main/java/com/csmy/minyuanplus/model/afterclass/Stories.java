package com.csmy.minyuanplus.model.afterclass;

import java.util.ArrayList;

/**
 * Created by Zero on 16/8/9.
 */
public class Stories{


    ArrayList<String> images = new ArrayList<>();
    int type;
    int id;
    String ga_prefix;
    String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
