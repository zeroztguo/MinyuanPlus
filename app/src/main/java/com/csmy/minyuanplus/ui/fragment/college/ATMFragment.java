package com.csmy.minyuanplus.ui.fragment.college;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.ATM;
import com.csmy.minyuanplus.model.ATMs;
import com.csmy.minyuanplus.support.adapter.ATMAdapter;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.google.gson.Gson;
import com.loopeer.cardstack.CardStackView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Zero on 16/8/19.
 */
public class ATMFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    @Bind(R.id.id_atm_srl)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.id_atm_csv)
    CardStackView mCardStackView;

    private ATMAdapter mAdapter;
    private List<ATM> mDatas;

    public static ATMFragment newInstance() {
        return new ATMFragment();
    }

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mAdapter = new ATMAdapter(getContext());
        mCardStackView.setAdapter(mAdapter);

        mDatas = DataSupport.findAll(ATM.class);
        if (mDatas.size() == 0) {
            obtainATMList();
        } else {
            mAdapter.updateData(mDatas);
        }

    }

    private void obtainATMList() {
        setRefresh(true);

        OkHttpUtils
                .get()
                .url("https://coding.net/u/zeroztguo/p/CollegePlus/git/raw/master/atm.txt")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        setRefresh(false);

                        Gson gson = new Gson();
                        ATMs atms = gson.fromJson(response, ATMs.class);
                        mDatas = atms.getAtmList();
                        DataSupport.deleteAll(ATM.class);
                        DataSupport.saveAll(mDatas);
                        mAdapter.updateData(mDatas);
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
