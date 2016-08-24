package com.csmy.minyuanplus.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.model.education.PersonalInfo;
import com.csmy.minyuanplus.support.adapter.DividerItemDecoration;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class UserInfoActivity extends BaseActivity {
    @Bind(R.id.id_user_info_rv)
    RecyclerView mRecyclerView;
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;

    private LinearLayoutManager mLinearLayoutManager;
    private CommonAdapter<Map<String,String>> mAdapter;
    private List<Map<String,String>> mDatas;
    private String[] mTitles ={"姓名","性别","民族","政治面貌","来源地区","来源省","学院","专业名称","行政班"};
    private String[] mContents;

    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_user_info;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar();
        initData();
        initRecyclerView();
    }

    private void initToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("个人信息");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new CommonAdapter<Map<String, String>>(this, R.layout.item_user_info,mDatas) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(viewHolder.getConvertView());
                return viewHolder;
            }

            @Override
            protected void convert(ViewHolder viewHolder, Map<String, String> item, int position) {
                viewHolder.setText(R.id.id_user_info_title_tv,item.get(KEY_TITLE));
                viewHolder.setText(R.id.id_user_info_content_tv,item.get(KEY_CONTENT));
            }

        };
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initData() {
        PersonalInfo pi = DataSupport.findFirst(PersonalInfo.class);
        mContents = new String[]{pi.getName(),pi.getSex()
                ,pi.getNation(),pi.getPoliticalStatus(),pi.getOriginArea()
                ,pi.getOriginProvince(),pi.getCollege(),pi.getMajor()
                ,pi.getClassInfo()};
        mDatas = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            Map<String,String> data = new HashMap<>();
            data.put(KEY_TITLE,mTitles[i]);
            data.put(KEY_CONTENT,mContents[i]);
            mDatas.add(data);
        }
    }
}
