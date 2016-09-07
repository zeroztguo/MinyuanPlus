package com.csmy.minyuanplus.model.afterclass;

import org.litepal.crud.DataSupport;

/**
 * Created by Zero on 16/8/9.
 */
public class Daily extends DataSupport {

    String image;
    int type;
    int id;
    String ga_prefix;
    String title;
    String id_str;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public Daily setId(int id) {
        this.id = id;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Daily setImage(String image) {
        this.image = image;
        return this;

    }

    public int getType() {
        return type;
    }

    public Daily setType(int type) {
        this.type = type;
        return this;

    }


    public String getGa_prefix() {
        return ga_prefix;
    }

    public Daily setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Daily setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getId_str() {
        return id_str;
    }

    public Daily setId_str(String id_str) {
        this.id_str = id_str;
        return this;
    }

    @Override
    public String toString() {
        return "Daily{" +
                "image='" + image + '\'' +
                ", type=" + type +
                ", id=" + id +
                ", ga_prefix='" + ga_prefix + '\'' +
                ", title='" + title + '\'' +
                ", id_str='" + id_str + '\'' +
                '}';
    }
}
