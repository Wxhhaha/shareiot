package com.wxh.shareiot.ui.adapter;

import android.content.Context;

import com.wxh.basiclib.adapter.BaseBindingAdapter;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.RechargeModel;
import com.wxh.shareiot.databinding.RechargeListItemLayoutBinding;

public class BalanceListAdapter extends BaseBindingAdapter<RechargeModel, RechargeListItemLayoutBinding> {
    public BalanceListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResId(int i) {
        return R.layout.recharge_list_item_layout;
    }

    @Override
    protected void onBindItem(RechargeListItemLayoutBinding rechargeListItemLayoutBinding, RechargeModel rechargeModel, int i) {
        rechargeListItemLayoutBinding.tvAmount.setText(String.format("%.2f", rechargeModel.getAmount() / 100f));
        rechargeListItemLayoutBinding.tvTime.setText(rechargeModel.getSuccessTime());
        switch (rechargeModel.getPlatform()) {
            case 0:
                rechargeListItemLayoutBinding.ivPlatform.setImageResource(R.mipmap.ic_wepay);
                break;
            case 1:
                rechargeListItemLayoutBinding.ivPlatform.setImageResource(R.mipmap.alipaylogon);
                break;
            case 2:
                rechargeListItemLayoutBinding.ivPlatform.setImageResource(R.mipmap.ic_paypal);
                break;
        }
    }
}
