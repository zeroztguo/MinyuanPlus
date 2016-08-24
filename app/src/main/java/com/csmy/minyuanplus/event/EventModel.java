package com.csmy.minyuanplus.event;

import java.util.List;

/**
 * Created by Zero on 16/5/28.
 */
public class EventModel<T> {
    private List<T> mDataList;
    private T mData;

    private int mEventCode = 0;


    public EventModel(int eventCode,List<T> dataList){
        this.mEventCode = eventCode;
        this.mDataList = dataList;
    }

    public EventModel(int eventMode,T data){
        this.mEventCode = eventMode;
        this.mData = data;
    }

    public EventModel(int eventMode){
        this.mEventCode = eventMode;
    }

    public T getData(){return mData;}

    public List<T> getDataList(){return mDataList;}

    public int getEventCode(){return mEventCode;}
}
