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
import com.csmy.minyuanplus.model.college.Express;
import com.csmy.minyuanplus.model.college.ExpressList;
import com.csmy.minyuanplus.support.API;
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
 * Express页面
 * Created by Zero on 16/8/19.
 */
public class ExpressFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.id_express_srl)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.id_express_rv)
    RecyclerView mRecyclerView;

    private List<Express> mDatas;
    private CommonAdapter<Express> mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Boolean> isShowPicList = new ArrayList<>();
    private List<Integer> idList = new ArrayList<>();


    public static ExpressFragment newInstance() {
        return new ExpressFragment();
    }

    @Override
    protected void initView(final View view, Bundle saveInstanceState) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorRed);

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mDatas = DataSupport.findAll(Express.class);

        for (Express data : mDatas) {
            isShowPicList.add(false);
            idList.add(data.getId());
        }


        mAdapter = new CommonAdapter<Express>(getContext(), R.layout.item_express, mDatas) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder holder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(holder.getConvertView());
                return holder;
            }


            @Override
            protected void convert(final ViewHolder viewHolder, final Express item, final int position) {

                if (isShowPicList.get(position)) {
                    viewHolder.setVisible(R.id.id_express_info_layout, false);
                    viewHolder.setVisible(R.id.id_express_pic_sdv, true);
//                    /*
//                    根据ID将Express查出来是因为图片是异步加载的
//                     */
                    Express Express = DataSupport.find(Express.class, idList.get(position));
                    Logger.d("更新时的id：" + Express.getId());
                    if (Express != null && Express.isHaveImg()) {
                        SimpleDraweeView draweeView = viewHolder.getView(R.id.id_express_pic_sdv);
                        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                        draweeView.setImageBitmap(Util.Bytes2Bitmap(Express.getImg()));
                        Logger.d("从本地加载Express图片~~~~~~~~~" + position);
                    }
                } else {
                    viewHolder.setVisible(R.id.id_express_info_layout, true);
                    viewHolder.setVisible(R.id.id_express_pic_sdv, false);
                    viewHolder.setText(R.id.id_express_name_tv, item.getName());
                    viewHolder.setText(R.id.id_express_time_tv, item.getTime());
                    viewHolder.setText(R.id.id_express_location_tv, item.getLocation());
                    viewHolder.setText(R.id.id_express_telephone_tv, item.getTelephone());
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

    private void obtainExpressList() {
        OkHttpUtils
                .get()
                .url(API.EXPRESS)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showShort(getContext(), getString(R.string.express_load_fail));
                        setRefresh(false);
                    }

                    @Override
                    public void onResponse(String response) {
                        setRefresh(false);

                        Gson gson = new Gson();
                        ExpressList expresses = gson.fromJson(response, ExpressList.class);


                        List<Express> expressList = expresses.getExpressList();

                        for (int i = 0; i < expressList.size(); i++) {
                            Express Express = expressList.get(i);
                            Express.setHaveImg(false);
                        }

                        DataSupport.deleteAll(Express.class);
                        DataSupport.saveAll(expressList);

                        /*
                        给数据源重新赋值是因为保存在数据库之后才会生成id
                         */
                        mDatas.clear();

                        mDatas.addAll(DataSupport.findAll(Express.class));

                        isShowPicList.clear();
                        idList.clear();
                        for (Express data : mDatas) {
                            isShowPicList.add(false);
                            idList.add(data.getId());
                        }

                        for (final Express express : mDatas) {
                            OkHttpUtils
                                    .get()
                                    .url(express.getImgUrl())
                                    .build()
                                    .execute(new BitmapCallback() {
                                        @Override
                                        public void onError(Call call, Exception e) {
                                            ToastUtil.showShort(getContext(), getString(R.string.express_pic_load_fail));
                                        }

                                        @Override
                                        public void onResponse(Bitmap response) {
                                            byte[] bytes = Util.Bitmap2Bytes(response);
                                            ContentValues values = new ContentValues();
                                            values.put("img", bytes);
                                            values.put("isHaveImg", true);
                                            DataSupport.update(Express.class, values, express.getId());
                                            Logger.d("从网络加载Express图片~~~~~~~~~成功");
                                        }
                                    });
                        }


                        Logger.d("加载Express信息成功，数量：" + mDatas.size());

                        mAdapter.notifyDataSetChanged();


                    }
                });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_express;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }

    @Override
    public void onRefresh() {
        obtainExpressList();
    }

    protected void setRefresh(boolean isRefresh) {
        mSwipeRefreshLayout.setRefreshing(isRefresh);
    }
}
