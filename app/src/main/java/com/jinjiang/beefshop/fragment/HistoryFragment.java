package com.jinjiang.beefshop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinjiang.beefshop.R;
import com.jinjiang.beefshop.sqlite.JjbeafSqlBean;
import com.jinjiang.beefshop.sqlite.SqliteUtil;
import com.jinjiang.beefshop.view.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.jinjiang.beefshop.view.recyclerview.HistoryOrderDataAdapter;

import java.util.List;

/**
 * 点单历史fragment
 */
public class HistoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<JjbeafSqlBean> jjbeafSqlBeen;
    private HistoryOrderDataAdapter mDataAdapter;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
    private TextView noData;
    private SwipeRefreshLayout mSwipeRefreshLaout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.history_rv_list);
        noData = (TextView) getView().findViewById(R.id.noData);
        mSwipeRefreshLaout = (SwipeRefreshLayout)getView().findViewById(R.id.lockgp_common_refreshlaout);
        mSwipeRefreshLaout.setColorSchemeColors(getContext().getResources().getColor(R.color.common_app_title_color));
        mSwipeRefreshLaout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    public void init(){
        jjbeafSqlBeen = SqliteUtil.getInstance().queryAllHistoryOrder(getContext());
        if(jjbeafSqlBeen != null && jjbeafSqlBeen.size() > 0){
            noData.setVisibility(View.GONE);
            mDataAdapter = new HistoryOrderDataAdapter(getContext());
            mDataAdapter.addAll(jjbeafSqlBeen);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mDataAdapter);
            mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }else {
            noData.setVisibility(View.VISIBLE);
        }
    }


    private void refresh() {
        jjbeafSqlBeen = SqliteUtil.getInstance().queryAllHistoryOrder(getContext());
        if(jjbeafSqlBeen != null && jjbeafSqlBeen.size() > 0){
            noData.setVisibility(View.GONE);
            mDataAdapter = new HistoryOrderDataAdapter(getContext());
            mDataAdapter.addAll(jjbeafSqlBeen);
            mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mDataAdapter);
            mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }else {
            noData.setVisibility(View.VISIBLE);
        }
        mSwipeRefreshLaout.setRefreshing(false);
    }

}
