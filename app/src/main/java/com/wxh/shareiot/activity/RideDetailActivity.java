package com.wxh.shareiot.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation;
import com.wxh.basiclib.base.BaseActivity;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.CycleOrderRecords;
import com.wxh.shareiot.api.CycleTrace;
import com.wxh.shareiot.api.CycleTraceResponse;
import com.wxh.shareiot.api.Error;
import com.wxh.shareiot.databinding.ActivityRideDetailBinding;
import com.wxh.shareiot.socket.JWebSocketClientService;
import com.wxh.shareiot.utils.DateUtil;
import com.wxh.shareiot.utils.MapBoxUtils;
import com.wxh.shareiot.utils.NumberUtil;
import com.wxh.shareiot.viewmodel.RideViewModel;

import java.util.ArrayList;
import java.util.List;

public class RideDetailActivity extends BaseActivity<RideViewModel, ActivityRideDetailBinding> {
    //    private GoogleMap gMap;
    private String cycleId;
    //    private List<LatLng> locations;
//    private Polyline polyline;
    private CycleOrderRecords record;
    private MapBoxUtils mapBoxUtils;
    private PolylineAnnotation polyline;
    private List<Point> locations;

    @Override
    protected int getLayout() {
        return R.layout.activity_ride_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mapBoxUtils = new MapBoxUtils(dataBinding.mapView, this,false, new MapBoxUtils.OnStyleLoadFinishListener() {
            @Override
            public void onFinish() {
                viewModel.findCycleTrace(cycleId);
            }
        }).hideLogo().hideScaleBar();
        dataBinding.ivBack.setOnClickListener(v -> finish());
//        //谷歌添加地图
//        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
//        getSupportFragmentManager().beginTransaction().add(R.id.my_container, mapFragment).commit();
//        mapFragment.getMapAsync(this);

        record = new Gson().fromJson(getIntent().getStringExtra("record"), CycleOrderRecords.class);
        if (record.getPayOrder().getOrderStatus() == 2) {
            dataBinding.tvPayStatus.setText(R.string.have_paid);
            dataBinding.btGoPay.setVisibility(View.GONE);
        } else {
            dataBinding.tvPayStatus.setText(R.string.have_not_pay);
            dataBinding.btGoPay.setVisibility(View.VISIBLE);
        }

        dataBinding.btGoPay.setOnClickListener(v -> {
            // 去支付页面支付
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra("order", new Gson().toJson(record.getPayOrder()));
            intent.putExtra("type", "order");
            startActivityForResult(intent, 0);
        });

        dataBinding.tvMoney.setText(String.format("%s", NumberUtil.div(record.getPayOrder().getAmount(), 100, 2)));

        dataBinding.tvStartTime.setText(record.getStartTime());
        dataBinding.tvEbikeNumber.setText(record.getEbikeId());
        dataBinding.tvSpendTime.setText(DateUtil.between(record.getStartTime(), record.getEndTime()));

        dataBinding.llOpenDetail.setOnClickListener(v -> {
            if (dataBinding.llDetail.getVisibility() == View.VISIBLE) {
                dataBinding.llDetail.setVisibility(View.GONE);
                dataBinding.ivArrow.setImageResource(R.mipmap.ic_down);
                dataBinding.tvOpenText.setText(R.string.open_detail);
            } else {
                dataBinding.llDetail.setVisibility(View.VISIBLE);
                dataBinding.ivArrow.setImageResource(R.mipmap.ic_up);
                dataBinding.tvOpenText.setText(R.string.close_detail);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //viewModel.findCycleTrace(cycleId);
        if (resultCode == RESULT_OK) {
            dataBinding.tvPayStatus.setText(R.string.have_paid);
            dataBinding.btGoPay.setVisibility(View.GONE);
        }

    }

    @Override
    protected void initData() {
        cycleId = getIntent().getStringExtra("cycleId");

        viewModel.getTraceListResult().observe(this, new Observer<CycleTraceResponse>() {
            @Override
            public void onChanged(CycleTraceResponse cycleTraceResponseMyResult) {
                List<CycleTrace> traceList = cycleTraceResponseMyResult.getEbikeGisList();
                locations = new ArrayList<>();
                for (CycleTrace trace : traceList) {
                    if (StringUtils.isEmpty(trace.getLat()) || StringUtils.isEmpty(trace.getLng())) {
                        continue;
                    }
                    double lat = Double.parseDouble(trace.getLat());
                    double lng = Double.parseDouble(trace.getLng());
                    if (lat == 0 || lng == 0) {
                        continue;
                    }
//                        double[] gps = GpsCoordinateUtils.calWGS84toGCJ02(lat,lng);
//                        LatLng latLng = new LatLng(gps[0],gps[1]);
                    locations.add(Point.fromLngLat(lng, lat));
                }
                drawTraces();
            }
        });
    }

    private void drawTraces() {
//        if (polyline == null) {
//            polyline = gMap.addPolyline(new PolylineOptions().color(Color.parseColor("#FFFF0000")).width(10).zIndex(0));
//        }
        if (locations == null || locations.size() == 0) {
            return;
        }
        mapBoxUtils.addLineMarker(locations.get(0),R.mipmap.ic_pop);
        mapBoxUtils.addLineMarker(locations.get(locations.size()-1),R.mipmap.ic_pop_red);
        polyline = mapBoxUtils.createPolyline(locations);
        polyline.setPoints(locations);
        List<List<Point>> list = new ArrayList<>();
        list.add(locations);
        Polygon polygon = Polygon.fromLngLats(list);
        mapBoxUtils.boundsCameraByPolyline(polygon);

//        polyline.setJointType(JointType.ROUND);
//        polyline.setStartCap(new CustomCap(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pop), 16));
//        polyline.setEndCap(
//                new CustomCap(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pop_red), 16));
//
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (LatLng point : locations) {
//            builder.include(point);
//        }
//        gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), ConvertUtils.dp2px(50)));
    }

    @Override
    protected RideViewModel initViewModel() {
        return ViewModelProviders.of(this).get(RideViewModel.class);
    }

    @Override
    protected void showError(Object o) {
        if (o instanceof Error) {
            Error error = (Error) o;
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
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        gMap = googleMap;
//        viewModel.findCycleTrace(cycleId);
//    }

    @SuppressLint("Lifecycle")
    @Override
    public void onStart() {
        super.onStart();
        dataBinding.mapView.onStart();
    }

    @SuppressLint("Lifecycle")
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        dataBinding.mapView.onLowMemory();
    }

    @SuppressLint("Lifecycle")
    @Override
    protected void onStop() {
        super.onStop();
        dataBinding.mapView.onStop();
    }

    @SuppressLint("Lifecycle")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataBinding.mapView.onDestroy();
    }
}