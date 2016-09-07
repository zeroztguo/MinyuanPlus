package com.csmy.minyuanplus.ui.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.csmy.minyuanplus.R;
import com.csmy.minyuanplus.event.Event;
import com.csmy.minyuanplus.model.NotifyContent;
import com.csmy.minyuanplus.ui.BaseToolbarView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import butterknife.Bind;

public class NotifyActivity extends BaseActivity implements BaseToolbarView {
    @Bind(R.id.id_notify_rv)
    RecyclerView mRecyclerView;
    @Bind(R.id.id_base_tool_bar)
    Toolbar mToolbar;

    private CommonAdapter<NotifyContent> mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<NotifyContent> mDatas;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_notify;
    }

    @Override
    protected int getFragmentContentId() {
        return 0;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initToolbar();
        initRecyclerView();
    }

    @Override
    public void initToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.notify_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView() {
//        mDatas = DataSupport.where("id >= ?", "0")
//                .order("id asc").find(NotifyContent.class);
        mDatas = DataSupport.findAll(NotifyContent.class);


        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new CommonAdapter<NotifyContent>(this, R.layout.item_notify, mDatas) {
            @Override
            protected void convert(final ViewHolder holder, NotifyContent notifyContent, final int position) {
                holder.setText(R.id.id_notify_title_tv, notifyContent.getTitle());
                holder.setText(R.id.id_notify_time_tv, notifyContent.getDate());
                if (notifyContent.isRead()) {
                    holder.setVisible(R.id.id_notify_is_read_color_view, false);
                } else {
                    holder.setVisible(R.id.id_notify_is_read_color_view, true);
                }

                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NotifyContent notifyContent = mDatas.get(position);
                        if (!notifyContent.isRead()) {
                            holder.setVisible(R.id.id_notify_is_read_color_view, false);
                            /*
                            设置消息为已读
                             */
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("isRead", true);
                            DataSupport.update(NotifyContent.class, contentValues, notifyContent.getId());
                            Event.sendEmptyMessage(Event.NOTIFY_UPDATE);
                        }

                        Intent intent = new Intent(NotifyActivity.this, NotifyContentActivity.class);
                        intent.putExtra("notify_content", notifyContent);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
                AutoUtils.auto(viewHolder.getConvertView());
                return viewHolder;
            }
        };

        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
