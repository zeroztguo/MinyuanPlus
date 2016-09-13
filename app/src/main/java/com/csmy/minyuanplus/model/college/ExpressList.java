package com.csmy.minyuanplus.model.college;

import java.util.ArrayList;

/**
 * Gson 解析快递数据的实体类
 * Created by Zero on 16/9/8.
 */
public class ExpressList {

    ArrayList<Express> expressList = new ArrayList<>();

    public ArrayList<Express> getExpressList() {
        return expressList;
    }

    public void setExpressList(ArrayList<Express> expressList) {
        this.expressList = expressList;
    }
}
