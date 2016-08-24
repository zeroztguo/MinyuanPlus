package com.csmy.minyuanplus.event;

import com.csmy.minyuanplus.support.util.SPUtil;

/**
 * Created by Zero on 16/6/29.
 */
public class EventTag {
    public static final int LOGIN_EDUCATION = 0;
    public static final int SWITCH_SCHEDULE = 1;
    public static final int QUERY_GRADE =2;
    public static final int GRADE_STATISTICAL =3;
    public static final int QUERY_GRADE_FAIL = 4;


    private static final String CURRENT_TAG = "current_tag";

    public static int getCurrentTag() {
        return (int) SPUtil.get(CURRENT_TAG,LOGIN_EDUCATION);
    }

    public static void saveCurrentTag(int currentTag) {
        SPUtil.put(CURRENT_TAG,currentTag);
    }
}
