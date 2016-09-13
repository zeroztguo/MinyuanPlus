package com.csmy.minyuanplus.model.afterclass;

import java.util.ArrayList;

/**
 * Gson解析果壳热门的实体
 * Created by Zero on 16/8/14.
 */
public class Guokr {
    ArrayList<GuokrHeader> result = new ArrayList<>();

    public ArrayList<GuokrHeader> getResult() {
        return result;
    }

    public void setResult(ArrayList<GuokrHeader> result) {
        this.result = result;
    }

}
