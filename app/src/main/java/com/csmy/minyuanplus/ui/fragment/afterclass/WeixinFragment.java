package com.csmy.minyuanplus.ui.fragment.afterclass;

import android.content.Intent;
import android.net.Uri;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.afterclass.Weixin;
import com.csmy.minyuanplus.model.afterclass.WeixinInfo;
import com.csmy.minyuanplus.support.API;
import com.csmy.minyuanplus.support.util.ToastUtil;
import com.csmy.minyuanplus.ui.activity.WeixinActivity;
import com.csmy.minyuanplus.ui.fragment.SwipeRereshFragment;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

import okhttp3.Call;

/**
 * 微信精选
 * Created by Zero on 16/9/3.
 */
public class WeixinFragment extends SwipeRereshFragment<Weixin> {
    private int mPage = 0;
    private boolean isLoadMore;

    public static WeixinFragment newInstance() {
        return new WeixinFragment();
    }


    @Override
    protected int getItemId() {
        return R.layout.item_after_class;
    }

    @Override
    protected void loadFromDatabase() {
        List<Weixin> weixinList = DataSupport.findAll(Weixin.class);
        addAllData(weixinList);
    }

    @Override
    protected void setItem(ViewHolder holder, Weixin weixin, int position) {
        SimpleDraweeView draweeView = holder.getView(R.id.id_after_class_image);
        Uri uri = Uri.parse(weixin.getPicUrl());
        draweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        draweeView.setImageURI(uri);

        holder.setText(R.id.id_after_class_title_tv, weixin.getTitle());
        holder.setText(R.id.id_after_class_author_tv, weixin.getDescription());
        holder.setText(R.id.id_after_class_time_tv, weixin.getCtime());
    }

    @Override
    protected void setOnItemClick(Weixin weixin) {
        Intent intent = new Intent(getHoldingActivity(), WeixinActivity.class);
        intent.putExtra(WeixinFragment.class.getSimpleName(), weixin);
        startActivity(intent);
    }

    @Override
    protected void refresh() {
        setRefresh();
        mPage = 1;
        isLoadMore = false;
        obtainWeixinList(mPage);
    }

    @Override
    protected void loadMore() {
        mPage++;
        isLoadMore = true;
        obtainWeixinList(mPage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel) {

    }


    private void obtainWeixinList(int page) {
        String url = API.WEIXIN + page;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        if (!isLoadMore) {
                            ToastUtil.showShort(getContext(), getString(R.string.minyuan_news_load_fail));
                            OkHttpUtils.getInstance().cancelTag(this);
                        }
                        setRefresh();
                    }

                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        WeixinInfo weixinInfo = gson.fromJson(response, WeixinInfo.class);
                        List<Weixin> weixinList = weixinInfo.getNewslist();

                        if (isLoadMore) {
                            DataSupport.saveAll(weixinList);
                            updateAllData(weixinList);
                        } else {
                            DataSupport.deleteAll(Weixin.class);
                            DataSupport.saveAll(weixinList);

                            addAllData(weixinList);
                            setRefresh();
                            setLoadMore();
                        }
                        Logger.d("微信精选一共有这么多条：" + weixinList.size());

                    }
                });
    }
}
