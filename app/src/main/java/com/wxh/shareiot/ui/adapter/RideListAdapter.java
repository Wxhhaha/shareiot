package com.wxh.shareiot.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.wxh.basiclib.adapter.BaseBindingAdapter;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.CycleOrderRecords;
import com.wxh.shareiot.databinding.RideItemBinding;
import com.wxh.shareiot.utils.DateUtil;
import com.wxh.shareiot.utils.NumberUtil;

public class RideListAdapter extends BaseBindingAdapter<CycleOrderRecords, RideItemBinding> {
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public RideListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return R.layout.ride_item;
    }

    @Override
    protected void onBindItem(RideItemBinding binding, CycleOrderRecords item,int i) {
        binding.tvStartTime.setText(item.getStartTime());
        binding.tvEbikeId.setText(item.getEbikeId());
        if (item.getEndTime() == null) {
            binding.tvSpend.setText(R.string.error_data);
            binding.tvMoney.setText("0");
        } else {
            binding.tvSpend.setText(DateUtil.between(item.getStartTime(), item.getEndTime()));
            binding.tvMoney.setText(String.format("%s", NumberUtil.div(item.getPayOrder().getAmount(), 100, 2)));
        }
        int status = item.getPayOrder().getOrderStatus();
        if (status == 2) {
            binding.tvStatus.setText(R.string.order_complete);
            binding.tvStatus.setTextColor(context.getColor(R.color.green));
            binding.tvGoPay.setVisibility(View.GONE);
        } else {
            binding.tvStatus.setText(R.string.have_not_pay);
            binding.tvStatus.setTextColor(Color.RED);
            binding.tvGoPay.setVisibility(View.VISIBLE);
        }
        binding.tvGoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecyclerViewItemClickListener.onItemClick(item);
            }
        });
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(CycleOrderRecords record);
    }
}
