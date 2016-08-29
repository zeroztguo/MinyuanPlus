package com.csmy.minyuanplus.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.KeyEvent;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.Notify;
import com.csmy.minyuanplus.model.NotifyContent;
import com.csmy.minyuanplus.support.Notification;
import com.csmy.minyuanplus.support.education.EduLogin;
import com.csmy.minyuanplus.support.util.SnackbarUtil;
import com.csmy.minyuanplus.ui.fragment.MoreFragment;
import com.csmy.minyuanplus.ui.fragment.afterclass.AfterClassFragment;
import com.csmy.minyuanplus.ui.fragment.college.CollegeFragment;
import com.csmy.minyuanplus.ui.fragment.collegenews.NewsFragment;
import com.csmy.minyuanplus.ui.fragment.education.EducationLoginFragment;
import com.csmy.minyuanplus.ui.fragment.education.ScheduleFragment;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;
import okhttp3.Call;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    @Bind(R.id.tab)
    PagerBottomTabLayout mBottomTabLayout;
    //    @BindColor(R.attr.tabSelectedTextColor)
    int textBottomTabSelected;
    //    @BindColor(R.color.textBottomTab)
    int colorTabText;

    Controller controller;

    private ScheduleFragment mHomeFragemnt;
    private EducationLoginFragment mEducationLoginFragment;
    private NewsFragment mNewsFragment;
    private AfterClassFragment mAfterClassFragment;
    private CollegeFragment mCollegeFragment;
    private MoreFragment mMoreFragment;
    private long mExitTime;


    @Override
    protected void initView(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        setSwipeEnabled(false);

        /*
        获取自定的颜色属性
         */
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.textBottomTabSelected, typedValue, true);
        textBottomTabSelected = typedValue.data;
        TypedValue typedValue2 = new TypedValue();
        getTheme().resolveAttribute(R.attr.textBottomTab, typedValue2, true);
        colorTabText = typedValue2.data;


        controller = mBottomTabLayout.builder()
                .addTabItem(R.mipmap.home, getString(R.string.home), textBottomTabSelected)
                .addTabItem(R.mipmap.news, getString(R.string.college_news), textBottomTabSelected)
                .addTabItem(R.mipmap.life, getString(R.string.after_class), textBottomTabSelected)
                .addTabItem(R.mipmap.social_work, getString(R.string.minyuan), textBottomTabSelected)
                .addTabItem(R.mipmap.others2, getString(R.string.more), textBottomTabSelected)
                .setDefaultColor(colorTabText)
//                .setMode(TabLayoutMode.HIDE_TEXT| TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                .build();

        /*
        如果保存过状态，弹出所有fragment并重新加载
         */
        if (savedInstanceState != null) {
            FragmentManager manager = getSupportFragmentManager();
            manager.popBackStackImmediate(null, 1);
        }

        OnTabItemSelectListener onTabItemSelectListener = new OnTabItemSelectListener() {
            @Override
            public void onSelected(int index, Object tag) {
                switch (index) {
                    case 0:
                        if (EduLogin.isEducationLogined()) {

                            if (mHomeFragemnt == null) {
                                mHomeFragemnt = ScheduleFragment.newInstance();
                                addFragment(mHomeFragemnt);
                            } else {

                                switchFragemnt(mHomeFragemnt);
                            }
                        } else {

                            if (mEducationLoginFragment == null) {

                                mEducationLoginFragment = EducationLoginFragment.newInstance();
                                addFragment(mEducationLoginFragment);
                            } else {


                                switchFragemnt(mEducationLoginFragment);
                            }
                        }

                        break;
                    case 1:
                        if (mNewsFragment == null) {

                            mNewsFragment = NewsFragment.newInstance();
                            addFragment(mNewsFragment);
                        } else {

                            switchFragemnt(mNewsFragment);
                        }
                        break;
                    case 2:
                        if (mAfterClassFragment == null) {

                            mAfterClassFragment = AfterClassFragment.newInstance();
                            addFragment(mAfterClassFragment);
                        } else {

                            switchFragemnt(mAfterClassFragment);
                        }
                        break;
                    case 3:
                        if (mCollegeFragment == null) {

                            mCollegeFragment = CollegeFragment.newInstance();
                            addFragment(mCollegeFragment);
                        } else {

                            switchFragemnt(mCollegeFragment);
                        }
                        break;
                    case 4:

                        if (mMoreFragment == null) {
                            mMoreFragment = MoreFragment.newInstance();
                            addFragment(mMoreFragment);
                        } else {

                            switchFragemnt(mMoreFragment);
                        }
                        break;
                }
            }


            @Override
            public void onRepeatClick(int index, Object tag) {
//
            }
        };


        controller.addTabItemClickListener(onTabItemSelectListener);
        initNotify();
    }

    private void initNotify() {
        Logger.d("init notify~~~~~~");
        OkHttpUtils
                .get()
                .url("https://coding.net/u/zeroztguo/p/CollegePlus/git/raw/master/notification.txt")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        Notify notify = gson.fromJson(response, Notify.class);
                        int latestNotifyCode = Integer.valueOf(notify.getLatestNotifyCode());
                        int localLatestNotifyCode = Notification.getLatestNotifyCode();

                        if (localLatestNotifyCode < latestNotifyCode) {
                            List<NotifyContent> notifyList = notify.getNotifyList();
                            /*
                            只保存本地没有的消息
                             */
                            for (NotifyContent notifyContent : notifyList) {
                                if(Integer.valueOf(notifyContent.getNotifyCode()) >localLatestNotifyCode){
                                    notifyContent.setRead(false);
                                    notifyContent.save();
                                }
                            }
//                            int unread = Notification.getUnreadNotify() + latestNotifyCode - localLatestNotifyCode;
//                            Notification.setUnreadNotify(unread);
                            Notification.setLatestNotifyCode(latestNotifyCode);
                            Event.sendEmptyMessage(Event.NOTIFY_UPDATE);
                        }
                    }
                });
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFragmentContentId() {
        return R.id.fragment_container;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {
        switch (eventModel.getEventCode()) {
            case Event.SWITCH_LOGIN_PAGE:
                controller.setSelect(0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                SnackbarUtil.showWithNoAction(mBottomTabLayout, "再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
