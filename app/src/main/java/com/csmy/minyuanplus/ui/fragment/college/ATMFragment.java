package com.csmy.minyuanplus.ui.fragment.college;

import android.os.Bundle;
import android.view.View;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.EventModel;
import com.csmy.minyuanplus.model.ATM;
import com.csmy.minyuanplus.support.adapter.ATMAdapter;
import com.csmy.minyuanplus.ui.fragment.BaseFragment;
import com.loopeer.cardstack.CardStackView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Zero on 16/8/19.
 */
public class ATMFragment extends BaseFragment {
    @Bind(R.id.id_atm_csv)
    CardStackView mCardStackView;
    
    private ATMAdapter mAdapter;
    private List<ATM> mDatas;

    public static ATMFragment newInstance(){return new ATMFragment();}

    @Override
    protected void initView(View view, Bundle saveInstanceState) {
        mAdapter = new ATMAdapter(getContext());
        mCardStackView.setAdapter(mAdapter);
        mDatas = new ArrayList<>();
 
        ATM atm = new ATM();
        atm.setInfo("ATM 测试1111111111111111111");
        ATM atm1 = new ATM();
        atm1.setInfo("ATM 测试2222222222222222222");
        ATM atm2 = new ATM();
        atm2.setInfo("ATM 测试33333333333333333333");
        mDatas.add(atm);
        mDatas.add(atm1);
        mDatas.add(atm2);
        mAdapter.updateData(mDatas);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_atm;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserEvent(EventModel eventModel){

    }

}
