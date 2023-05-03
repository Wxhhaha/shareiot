package com.wxh.shareiot.utils;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class GLocOverlay {
    private LatLng point;
    private float radius;
    private Marker locMarker;
    //    private Marker locBearingMarker;
//    private Circle locCircle;
    private GoogleMap map;

    public GLocOverlay(GoogleMap map) {
        this.map = map;
    }

    public void locationChanged(LatLng glocation) {
        // LatLng location = new LatLng(glocation.getLatitude(), glocation.getLongitude());
        this.point = glocation;
//        this.radius = glocation.getAccuracy();
        MapUtils.setCenterByLatLng(map, point, map.getMaxZoomLevel()-3);
        if (locMarker == null) {
            addMarker();
        }
//        if (locBearingMarker == null) {
//            addBearingMarker();
//        }
//        if (locCircle == null) {
//            addCircle();
//        }
//        float bearing = glocation.getBearing();
//        locBearingMarker.setRotation(bearing);
        moveLocationMarker();
//        locCircle.setRadius(radius);
    }

    /**
     * 平滑移动动画
     */
    private void moveLocationMarker() {
        final LatLng startPoint = locMarker.getPosition();
        final LatLng endPoint = point;
        float rotate = getRotate(startPoint, endPoint);
//        locBearingMarker.setRotation(360 - rotate + map.getCameraPosition().bearing);
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LatLng target = (LatLng) valueAnimator.getAnimatedValue();
//                if (locCircle != null) {
//                    locCircle.setCenter(target);
//                }
                if (locMarker != null) {
                    locMarker.setPosition(target);
                }
//                if (locBearingMarker != null) {
//                    locBearingMarker.setPosition(target);
//                }
            }
        });
        anim.setDuration(1000);
        anim.start();
    }

    /**
     * 添加定位marker
     */
    private void addMarker() {
        locMarker = MapUtils.addLocationMarker(map, point);
    }

    /**
     * 添加指向点marker
     */
//    private void addBearingMarker() {
//        locBearingMarker = MapUtils.addLocationMarkerPoint(map, point);
//    }

    /**
     * 添加定位精度圈
     */
//    private void addCircle() {
//        locCircle = MapUtils.addLocationCircle(map, point, radius);
//    }
    public void remove() {
        if (locMarker != null) {
            locMarker.remove();
            locMarker = null;
        }
//        if (locBearingMarker != null) {
//            locBearingMarker.remove();
//            locBearingMarker = null;
//        }
//        if (locCircle != null) {
//            locCircle.remove();
//            locCircle = null;
//        }
    }

    public class PointEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            LatLng startPoint = (LatLng) startValue;
            LatLng endPoint = (LatLng) endValue;
            double x = startPoint.latitude + fraction * (endPoint.latitude - startPoint.latitude);
            double y = startPoint.longitude + fraction * (endPoint.longitude - startPoint.longitude);
            LatLng point = new LatLng(x, y);
            return point;
        }
    }

    /**
     * 根据经纬度计算需要偏转的角度
     *
     * @param curPos
     * @param nextPos
     * @return
     */
    private float getRotate(LatLng curPos, LatLng nextPos) {
        if (curPos == null || nextPos == null) {
            return 0;
        }
        double x1 = curPos.latitude;
        double x2 = nextPos.latitude;
        double y1 = curPos.longitude;
        double y2 = nextPos.longitude;

        float rotate = (float) (Math.atan2(y2 - y1, x2 - x1) / Math.PI * 180);
        return rotate;
    }
}
