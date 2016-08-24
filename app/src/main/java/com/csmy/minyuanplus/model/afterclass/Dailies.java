package com.csmy.minyuanplus.model.afterclass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zero on 16/8/9.
 */
public class Dailies {
    ArrayList<Stories> stories = new ArrayList<Stories>();
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Stories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<Stories> stories) {
        this.stories = stories;
    }

}
