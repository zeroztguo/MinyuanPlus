package com.csmy.minyuanplus.ui.fragment.college;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.college.ATM;
import com.csmy.minyuanplus.model.college.ATMs;
import com.csmy.minyuanplus.support.API;
import com.csmy.minyuanplus.support.SettingConfig;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.support.util.Util;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * ATM页面
 * Created by Zero on 16/8/19.
 */

public class ATMFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.id_atm_srl)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.id_atm_rv)
    RecyclerView mRecyclerView;

    private List<ATM> mDatas;
    private CommonAdapter<ATM> mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Boolean> isShowPicList = new ArrayList<>();
    private List<Integer> idList = new ArrayList<>();


    public static ATMFragment newInstance() {
        return new ATMFragment();
    }

    @Override
    protected void initView(final View view, Bundle saveInstanceState) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(SettingConfig.themeColorArray[SettingConfig.getThemeIndex()]);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mDatas = DataSupport.findAll(ATM.class);

        for (ATM data : mDatas) {
            isShowPicList.add(false);
            idList.add(data.getId());
        }


        mAdapter = new CommonAdapter<ATM>(getContext(), R.layout.item_atm, mDatas) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder holder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(holder.getConvertView());
                return holder;
            }


            @Override
            protected void convert(final ViewHolder viewHolder, final ATM item, final int position) {

                if (isShowPicList.get(position)) {
                    viewHolder.setVisible(R.id.id_atm_info_layout, false);
                    viewHolder.setVisible(R.id.id_atm_pic_sdv, true);
//                    /*
//                    根据ID将ATM查出来是因为图片是异步加载的
//                     */
                    ATM atm = DataSupport.find(ATM.class, idList.get(position));
                    Logger.d("更新时的id：" + atm.getId());
                    if (atm != null && atm.isHaveImg()) {
                        SimpleDraweeView draweeView = viewHolder.getView(R.id.id_atm_pic_sdv);
                        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                        draweeView.setImageBitmap(Util.Bytes2Bitmap(atm.getImg()));
                        Logger.d("从本地加载ATM图片~~~~~~~~~" + position);
                    }
                } else {
                    viewHolder.setVisible(R.id.id_atm_info_layout, true);
                    viewHolder.setVisible(R.id.id_atm_pic_sdv, false);
                    viewHolder.setText(R.id.id_atm_info_tv, item.getInfo());
                    viewHolder.setText(R.id.id_atm_location_tv, item.getLocation());
                }
            }
        };


        mAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(View view, RecyclerView.ViewHolder holder, Object o, int position) {
                                                isShowPicList.set(position, !isShowPicList.get(position));
                                                mAdapter.notifyItemChanged(position);
                                            }

                                            @Override
                                            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, Object o,
                                                                           int position) {
                                                return false;
                                            }
                                        }

        );


        mRecyclerView.setAdapter(mAdapter);

        if (mDatas.size() == 0)

        {

            mSwipeRefreshLayout.setProgressViewOffset(false, 0, 100);
            setRefresh(true);
            onRefresh();
        }

    }

    private void obtainATMList() {
        Logger.d("开始从网络获取ATM信息。。。");
        OkHttpUtils
                .get()
                .url(API.ATM)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showShort(getContext(), getString(R.string.atm_load_fail));
                        setRefresh(false);
                    }

                    @Override
                    public void onResponse(String response) {
                        setRefresh(false);

                        Gson gson = new Gson();
                        ATMs atms = gson.fromJson(response, ATMs.class);


                        List<ATM> atmList = atms.getAtmList();

                        for (int i = 0; i < atmList.size(); i++) {
                            ATM atm = atmList.get(i);
                            atm.setHaveImg(false);
                        }

                        DataSupport.deleteAll(ATM.class);
                        DataSupport.saveAll(atmList);

                        /*
                        给数据源重新赋值是因为保存在数据库之后才会生成id
                         */
                        mDatas.clear();
                        mDatas.addAll(DataSupport.findAll(ATM.class));

                        isShowPicList.clear();
                        idList.clear();

                        for (ATM data : mDatas) {
                            isShowPicList.add(false);
                            idList.add(data.getId());
                        }

                        mAdapter.notifyDataSetChanged();

                        for (final ATM atm : mDatas) {
                            OkHttpUtils
                                    .get()
                                    .url(atm.getImgUrl())
                                    .build()
                                    .execute(new BitmapCallback() {
                                        @Override
                                        public void onError(Call call, Exception e) {
                                            ToastUtil.showShort(getContext(), getString(R.string.atm_pic_load_fail));
                                        }

                                        @Override
                                        public void onResponse(Bitmap response) {
                                            byte[] bytes = Util.Bitmap2Bytes(response);
                                            ContentValues values = new ContentValues();
                                            values.put("img", bytes);
                                            values.put("isHaveImg", true);
                                            DataSupport.update(ATM.class, values, atm.getId());
                                            Logger.d("从网络加载ATM图片~~~~~~~~~成功");
                                        }
                                    });
                        }

                        Logger.d("加载ATM信息成功，数量：" + mDatas.size());

                    }
                });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_atm;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    @Override
    public void onRefresh() {
        obtainATMList();
    }

    protected void setRefresh(boolean isRefresh) {
        mSwipeRefreshLayout.setRefreshing(isRefresh);
    }

}
