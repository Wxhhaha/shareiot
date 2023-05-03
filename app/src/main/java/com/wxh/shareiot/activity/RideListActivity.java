package com.wxh.shareiot.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.basiclib.view.XRecyclerView;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.CycleListResponse;
import com.wxh.shareiot.api.CycleOrderPage;
import com.wxh.shareiot.api.CycleOrderRecords;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.databinding.ActivityRideListBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.ui.adapter.RideListAdapter;
import com.wxh.shareiot.viewmodel.RideViewModel;

public class RideListActivity extends BaseActivity<RideViewModel, ActivityRideListBinding> {

    private RideListAdapter adapter;
    private XRecyclerView xRecyclerView;
    private boolean isRefresh;
    private int pageNum = 0;
    private int total;

    @Override
    protected int getLayout() {
        return R.layout.activity_ride_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        xRecyclerView = dataBinding.xrv;
        adapter = new RideListAdapter(this);
//        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.list_divider);
//        xRecyclerView.addItemDecoration(xRecyclerView.new DividerItemDecoration(dividerDrawable));

        xRecyclerView.setPullRefreshEnabled(true);
        xRecyclerView.setLoadingMoreEnabled(true);
        xRecyclerView.setAdapter(adapter);
        adapter.setOnRecyclerViewItemClickListener(new RideListAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(CycleOrderRecords record) {
                //跳至支付页面
                Intent intent = new Intent(getApplicationContext(),PaymentActivity.class);
                intent.putExtra("order", new Gson().toJson(record.getPayOrder()));
                intent.putExtra("type", "order");
                startActivityForResult(intent, 0);
            }
        });

        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                pageNum = 1;
                xRecyclerView.setNoMore(false);
                getCycleList();
            }

            @Override
            public void onLoadMore() {
                isRefresh = false;
                getCycleList();
            }
        });

        dataBinding.topAppBar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    private void getCycleList() {
        if (isRefresh) {
            pageNum = 1;
        } else {
            pageNum++;
        }
        LogUtils.e("pageNum="+pageNum);
        viewModel.findCycleList(pageNum);
    }

    @Override
    protected void initData() {
        viewModel.getCycleListResult().observe(this, new Observer<CycleListResponse>() {
            @Override
            public void onChanged(CycleListResponse cycleListResponse) {
                if (xRecyclerView != null) {
                    xRecyclerView.refreshComplete();
                }
                    if (isRefresh) {
                        adapter.getItems().clear();
                    }
                    LogUtils.e(new Gson().toJson(cycleListResponse.getCycleOrderPage()));
                    CycleOrderPage cycleOrderPage = cycleListResponse.getCycleOrderPage();
                    if (cycleOrderPage.getPages() == cycleOrderPage.getCurrent()) {
                        xRecyclerView.setNoMore(true);
                    }
                    adapter.getItems().addAll(cycleOrderPage.getRecords());
                    adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRefresh = true;
        getCycleList();
    }

    @Override
    protected RideViewModel initViewModel() {
        return ViewModelProviders.of(this).get(RideViewModel.class);
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