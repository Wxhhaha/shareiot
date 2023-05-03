package com.wxh.shareiot.utils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wxh.shareiot.R;

import java.util.List;

/**
 * 创建时间:2021/12/2 8:46
 * 作者:wxh
 */
public class MapUtils {

    /**
     * 重新设置地图中心点
     *
     * @param map
     * @param latLng
     */
    public static void setCenterByLatLng(GoogleMap map, LatLng latLng, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * 向添加标记点
     *
     * @param map
     * @param latLng
     * @param drawableId
     */
    public static Marker addMarker(GoogleMap map, LatLng latLng, int drawableId) {
        return map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(drawableId)).draggable(false));
    }

    public static void changeMarkerIcon(int drawableId, Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.fromResource(drawableId));
    }

    /**
     * 清空地图上的组件
     *
     * @param map
     */
    public static void onClearMap(GoogleMap map) {
        if (map != null) {
            map.clear();
        }
    }

    /**
     * 在地图上画圆
     *
     * @param map
     * @param latLng
     * @param radius 单位 米
     * @return
     */
    public static Circle addLocationCircle(GoogleMap map, LatLng latLng, double radius) {
        CircleOptions circleOptions =
                new CircleOptions().center(latLng).radius(radius).fillColor(0x205D9CEC).strokeColor(0xff5D9CEC).strokeWidth(2);
        return map.addCircle(circleOptions);
    }

    /**
     * 添加当前位置点
     *
     * @param map
     * @param latLng
     * @return
     */
    public static Marker addLocationMarker(GoogleMap map, LatLng latLng) {
        return map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_dot)).anchor(0.5f, 0.5f).draggable(false));
    }

    /**
     * 添加当前位置方向点
     *
     * @param map
     * @param latLng
     * @return
     */
    public static Marker addLocationMarkerPoint(GoogleMap map, LatLng latLng) {
        return map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pointer)).draggable(false));
    }

    /**
     * 添加多边形
     *
     * @param map
     * @param points
     * @param holes
     * @return
     */
    public static Polygon addPolygon(GoogleMap map, List<LatLng> points, List<List<LatLng>> holes) {
        PolygonOptions options = new PolygonOptions();
        options.addAll(points);
        if (holes != null) {
            for (List<LatLng> hole : holes) {
                options.addHole(hole);
            }
        }
        return map.addPolygon(options);
    }

    /**
     * 画线
     *
     * @param map
     * @param points
     * @return
     */
    public static Polyline addPolyline(GoogleMap map, List<LatLng> points) {
        PolylineOptions options = new PolylineOptions();
        options.addAll(points);
        return map.addPolyline(options);
    }


}
