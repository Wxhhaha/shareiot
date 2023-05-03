package com.wxh.shareiot.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.basiclib.view.XRecyclerView;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.databinding.ActivityBalanceBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.ui.adapter.BalanceListAdapter;
import com.wxh.shareiot.viewmodel.BalanceViewModel;

import org.json.JSONObject;

public class BalanceListActivity extends BaseActivity<BalanceViewModel, ActivityBalanceBinding> {
    private BalanceListAdapter adapter;
    private XRecyclerView xRecyclerView;
    private boolean isRefresh;
    private int pageNum = 0;

    @Override
    protected int getLayout() {
        return R.layout.activity_balance;
    }

    @Override
    protected void initView(Bundle bundle) {
        xRecyclerView = dataBinding.xrv;
        adapter = new BalanceListAdapter(this);
        xRecyclerView.setPullRefreshEnabled(true);
        xRecyclerView.setLoadingMoreEnabled(true);
        xRecyclerView.setAdapter(adapter);

        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                pageNum = 1;
                xRecyclerView.setNoMore(false);
                getRechargeList();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getRechargeList();
            }
        });

        dataBinding.topAppBar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void initData() {
        getRechargeList();
        viewModel.getRechargeListResult().observe(this, rechargeListResponseMyResult -> {
            if (xRecyclerView != null) {
                xRecyclerView.refreshComplete();
            }
            if (isRefresh) {
                adapter.getItems().clear();
            }
            if (rechargeListResponseMyResult.getTotal() <= pageNum * 20) {
                xRecyclerView.setNoMore(true);
            }
            adapter.getItems().addAll(rechargeListResponseMyResult.getList());
        });
    }

    private void getRechargeList() {
        if (isRefresh) {
            pageNum = 1;
        } else {
            pageNum++;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("status", 1);
            object.put("pageNumber", pageNum);
            object.put("pageSize", 20);
            viewModel.getRechargeList(object);
        } catch (Exception ignored) {

        }
    }

    @Override
    protected BalanceViewModel initViewModel() {
        return ViewModelProviders.of(this).get(BalanceViewModel.class);
    }

    @Override
    protected void showError(Object o) {
        if(o instanceof Error){
            Error error = (Error)o;
            switch (error.getCode()) {
                case 401:
                    ToastUtils.showShort(error.getMsg());
                    //令牌失效
                    stopService(new Intent(this, JWebSocketClientService.class));
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ActivityUtils.finishAllActivities();
                    break;
                case 403:
                    ToastUtils.showShort("无权访问");
                    break;
                case 504:
                    ToastUtils.showShort("timeout");
                    break;
                default:
                    ToastUtils.showShort(((Error) o).getMsg());
                    break;
            }
        }
    }
}
