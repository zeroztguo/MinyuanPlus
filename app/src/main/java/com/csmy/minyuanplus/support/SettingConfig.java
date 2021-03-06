package com.csmy.minyuanplus.support;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.education.EduLogin;
import com.csmy.minyuanplus.support.util.SPUtil;

import org.litepal.crud.DataSupport;

/**
 * 设置配置
 * Created by Zero on 16/8/18.
 */
public class SettingConfig {
    //是否省流量
    private static final String IS_SAVE_FLOW = "is_save_flow";
    //是否夜间模式
    private static final String IS_NIGHT_MODE = "is_night_mode";
    //头像下标
    private static final String USER_ICON_INDEX = "user_icon_index";
    //主题下标
    private static final String THEME_INDEX = "theme_index";

    //语言
    public static final String LANGUAGE = "language";
    //中文简体
    public static final String ZH_SIMPLE = "zh_simple";
    //中文繁体
    public static final String ZH_TW = "zh_tw";
    //英文
    public static final String EN = "en";


    /**
     * 头像
     */
    public static Integer[] userIconArray = new Integer[]{
            R.mipmap.logo_blue, R.mipmap.logo_red,
            R.mipmap.baike, R.mipmap.konglianshun,
            R.mipmap.gaoguai_boy, R.mipmap.gaoguai_girl,
            R.mipmap.shashou_boy, R.mipmap.shashou_girl,
            R.mipmap.erkang, R.mipmap.rongmomo,
            R.mipmap.wangzaiboy, R.mipmap.wangzaigirl
    };

    /**
     * 主题
     */
    public static Integer[] themeArray = new Integer[]{R.style.AppTheme, R.style.AppTheme_Red
            , R.style.AppTheme_Pink, R.style.AppTheme_Purple
            , R.style.AppTheme_DeepPurple, R.style.AppTheme_Indigo
            , R.style.AppTheme_Cyan, R.style.AppTheme_Teal
            , R.style.AppTheme_Green, R.style.AppTheme_LightGreen
            , R.style.AppTheme_Lime, R.style.AppTheme_Yellow
            , R.style.AppTheme_Amber, R.style.AppTheme_Orange
            , R.style.AppTheme_DeepOrange, R.style.AppTheme_Brown
            , R.style.AppTheme_Grey, R.style.AppTheme_BlueGrey};

    /**
     * 主题颜色
     */
    public static Integer[] themeColorArray = new Integer[]{R.color.colorPrimary, R.color.colorRed
            , R.color.colorPink, R.color.colorPurple
            , R.color.colorDeepPurple, R.color.colorIndigo
            , R.color.colorCyan, R.color.colorTeal
            , R.color.colorGreen, R.color.colorLightGreen
            , R.color.colorLime, R.color.colorYellow
            , R.color.colorAmber, R.color.colorOrange
            , R.color.colorDeepOrange, R.color.colorBrown
            , R.color.colorGrey, R.color.colorBlueGrey};

    public static Integer[] getUserIconArray() {
        return userIconArray;
    }

    /**
     * @return 是否夜间模式
     */
    public static boolean isNightMode() {
        return (boolean) SPUtil.get(IS_NIGHT_MODE, false);
    }

    /**
     * 设置是否夜间模式
     */
    public static void setNightMode(boolean nightMode) {
        SPUtil.put(IS_NIGHT_MODE, nightMode);
    }

    /**
     * @return 是否省流量模式
     */
    public static boolean isSaveFlow() {
        return (boolean) SPUtil.get(IS_SAVE_FLOW, false);
    }

    /**
     * 设置是否省流量模式
     */
    public static void setSaveFlow(boolean saveFlow) {
        SPUtil.put(IS_SAVE_FLOW, saveFlow);
    }

    /**
     * 设置头像下标
     *
     * @param index
     */
    public static void setUserIconIndex(int index) {
        SPUtil.put(USER_ICON_INDEX, index);
    }

    public static int getUserIconIndex() {
        int defaultIndex;
        if (EduLogin.isEducationLogined()) {
            PersonalInfo personalInfo = DataSupport.findAll(PersonalInfo.class).get(0);
            if (personalInfo.getSex().equals("男")) {
                defaultIndex = 0;
            } else {
                defaultIndex = 1;
            }
        } else {
            defaultIndex = 0;
        }
        return (int) SPUtil.get(USER_ICON_INDEX, defaultIndex);
    }

    /**
     * 设置主题下标
     *
     * @param index
     */
    public static void setThemeIndex(int index) {
        SPUtil.put(THEME_INDEX, index);
    }

    public static int getThemeIndex() {
        return (int) SPUtil.get(THEME_INDEX, 0);
    }

    /**
     * 设置语言
     *
     * @param language
     */
    public static void setLanguage(String language) {
        SPUtil.put(LANGUAGE, language);
    }

    public static String getLanguage() {
        return (String) SPUtil.get(LANGUAGE, ZH_SIMPLE);
    }

}
