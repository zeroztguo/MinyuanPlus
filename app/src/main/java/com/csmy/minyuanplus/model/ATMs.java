package com.csmy.minyuanplus.model;

import java.util.ArrayList;

/**
 * Created by Zero on 16/8/29.
 */
public class ATMs {
    ArrayList<ATM> atmList = new ArrayList<>();

    public ArrayList<ATM> getAtmList() {
        return atmList;
    }

    public void setAtmList(ArrayList<ATM> atmList) {
        this.atmList = atmList;
    }
}
