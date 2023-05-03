package com.wxh.shareiot.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.blankj.utilcode.util.ImageUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationAnchor;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap;
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions;
import com.mapbox.maps.plugin.attribution.AttributionPlugin;
import com.mapbox.maps.plugin.compass.CompassPlugin;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationProvider;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.wxh.basiclib.utils.Dp2pxUtils;
import com.wxh.shareiot.R;
import com.wxh.shareiot.api.EbikeGis;
import com.wxh.shareiot.databinding.TopInfoLayoutBinding;

import java.util.Collections;
import java.util.List;

public class MapBoxUtils {
    private static final double LOCATE_ZOOM_VALUE = 14.0;   // 定位后地图缩放大小
    private GesturesPlugin gesturesPlugin;
    private LocationComponentPlugin locationPlugin;
    private PointAnnotationManager pointAnnotationManager;
    private PolylineAnnotationManager polylineAnnotationManager;
    private PolygonAnnotationManager polygonAnnotationManager;
    private ViewAnnotationManager viewAnnotationManager;
    private boolean isCameraFollowUser = true;
    private MapView mapView;
    private Context context;
    private MapboxMap mapboxMap;
    private OnStyleLoadFinishListener onStyleLoadFinishListener; // 地图加载完毕回调
    private Point userLocation;
    private boolean isLocate;
    private LocationChanged locationChanged;

    public LocationChanged getLocationChanged() {
        return locationChanged;
    }

    public void setLocationChanged(LocationChanged locationChanged) {
        this.locationChanged = locationChanged;
    }

    public interface OnStyleLoadFinishListener {
        void onFinish();
    }

    public MapBoxUtils(MapView mapView, Context context, boolean islocate,
                       OnStyleLoadFinishListener listener) {
        this.mapView = mapView;
        this.context = context;
        this.mapboxMap = mapView.getMapboxMap();
        this.onStyleLoadFinishListener = listener;
        this.isLocate = islocate;
        mapInit();
    }

    private void mapInit() {
        if (isLocate) {
            initLocationComponent();
            setupGesturesListener();
        }
        setAnnotationManagers();
        mapboxMap.setCamera(new CameraOptions.Builder().zoom(14.0).build());
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS, style -> {
            if (onStyleLoadFinishListener != null) {
                onStyleLoadFinishListener.onFinish();
            }
        });
        mapView.setOnClickListener(view -> {
            clearViewAnnotations();
        });
    }

    private void setupGesturesListener() {
        gesturesPlugin = mapView.getPlugin(Plugin.MAPBOX_GESTURES_PLUGIN_ID);
        assert gesturesPlugin != null;
        gesturesPlugin.addOnMoveListener(onMoveListener);
        gesturesPlugin.addOnMapClickListener(new OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull Point point) {
                refreshBikeMarkers();
                return false;
            }
        });
    }

    private void setAnnotationManagers() {
        AnnotationPlugin annotationApi = mapView.getPlugin(Plugin.MAPBOX_ANNOTATION_PLUGIN_ID);
        assert annotationApi != null;
        pointAnnotationManager = (PointAnnotationManager) annotationApi.createAnnotationManager(AnnotationType.PointAnnotation, new AnnotationConfig());
        polylineAnnotationManager = (PolylineAnnotationManager) annotationApi.createAnnotationManager(AnnotationType.PolylineAnnotation, new AnnotationConfig());
        polylineAnnotationManager.setLineCap(LineCap.ROUND);
        polygonAnnotationManager = (PolygonAnnotationManager) annotationApi.createAnnotationManager(AnnotationType.PolygonAnnotation, new AnnotationConfig());
        viewAnnotationManager = mapView.getViewAnnotationManager();
    }

    private void createViewAnnotation(PointAnnotation point) {
        View viewAnnotation = viewAnnotationManager.addViewAnnotation(R.layout.top_info_layout, new ViewAnnotationOptions.Builder().geometry(point.getPoint()).associatedFeatureId(point.getFeatureIdentifier()).anchor(ViewAnnotationAnchor.BOTTOM).offsetY(point.getIconImageBitmap().getHeight()).build());
        TopInfoLayoutBinding binding = TopInfoLayoutBinding.bind(viewAnnotation);
        JsonElement element = point.getData();
        if (element.isJsonObject()) {
            EbikeGis gis = new Gson().fromJson(element, EbikeGis.class);
            binding.tvSoc.setText(gis.getSoc() + "%");
            binding.tvCode.setText(gis.getEbikeId());
        }
    }

    private void clearViewAnnotations() {
        viewAnnotationManager.removeAllViewAnnotations();
    }

    private OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            onCameraTrackingDismissed();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };
    private OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = new OnIndicatorBearingChangedListener() {
        @Override
        public void onIndicatorBearingChanged(double v) {
            if (isCameraFollowUser) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().bearing(v).build());
            }
        }
    };

    private OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
            if (isCameraFollowUser) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().center(point).build());
                gesturesPlugin.setFocalPoint(mapView.getMapboxMap().pixelForCoordinate(point));
            }
            if(userLocation==null||!userLocation.equals(point)){
                if(locationChanged!=null){
                    locationChanged.onChanged(point);
                }
            }
            userLocation = point;
        }
    };

    public void onCameraTrackingDismissed() {
        isCameraFollowUser = false;
        gesturesPlugin.removeOnMoveListener(onMoveListener);
    }


    public void initLocationComponent() {
        if (locationPlugin == null) {  // 初始化
            locationPlugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
            if (locationPlugin == null) {
                return;
            }
        }
       // LocationComponentUtils.createDefault2DPuck(locationPlugin, context, true);

        // locationPlugin.setLocationPuck(new LocationPuck2D(null,AppCompatResources.getDrawable(context, R.drawable.mapbox_user_puck_icon),null));
        locationPlugin.setEnabled(true);
        locationPlugin.updateSettings(locationComponentSettings -> {
            locationComponentSettings.setEnabled(true);
            locationComponentSettings.setLocationPuck(new LocationPuck2D(null, AppCompatResources.getDrawable(context, R.mipmap.ic_mapbox_user_locate),
                    null, "[\"interpolate\",[\"linear\"],[\"zoom\"],0.0,1.0,10.0,0.8]"));
            return null;
        });
        locationPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
        locationPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
    }

    public void setLocationProvider(LocationProvider locationProvider) {
        locationPlugin.setLocationProvider(locationProvider);
    }

    // 隐藏地图左下角logo
    public MapBoxUtils hideLogo() {
//        // 根据官网使用要求，不建议隐藏
//        LogoPlugin logoPlugin =  mMapView.getPlugin(Plugin.MAPBOX_LOGO_PLUGIN_ID);
//        if (logoPlugin != null) {
//            logoPlugin.setEnabled(false);
//        }
        AttributionPlugin attributionPlugin = mapView.getPlugin(Plugin.MAPBOX_ATTRIBUTION_PLUGIN_ID);
        if (attributionPlugin != null) {
            attributionPlugin.setEnabled(false);
        }
        return this;
    }

    public MapBoxUtils setCompass() {
        CompassPlugin compassPlugin = mapView.getPlugin(Plugin.MAPBOX_COMPASS_PLUGIN_ID);
        if (compassPlugin != null) {
            compassPlugin.setImage(context.getDrawable(R.drawable.ic_compass));
            compassPlugin.setMarginRight(Dp2pxUtils.dip2px(context, 20));
            compassPlugin.setMarginTop(Dp2pxUtils.dip2px(context, 20));
        }
        return this;
    }

    // 隐藏地图左上角比例尺
    public MapBoxUtils hideScaleBar() {
        ScaleBarPlugin scaleBarPlugin = mapView.getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
        if (scaleBarPlugin != null) {
            scaleBarPlugin.setEnabled(false);
        }
        return this;
    }

    /**
     * 将摄像头移动到用户位置
     */
    public void moveCameraToUserPos() {
        if (userLocation == null) {
            return;
        }
        moveCameraTo(userLocation);
    }

    /**
     * 将摄像头移动到指定位置
     *
     * @param point 目标坐标
     */
    public void moveCameraTo(Point point) {
        if (mapView == null) {
            return;
        }
        mapboxMap.setCamera(new CameraOptions.Builder()
                .center(point)
                .build());
    }

    public void boundsCameraByPoints(List<Point> pointList) {
        float padding = Dp2pxUtils.dip2px(context, 50);
        CameraOptions options = mapboxMap.cameraForCoordinates(pointList, new EdgeInsets(padding, padding, padding, padding), 0.0, 0.0);
        mapboxMap.setCamera(options);
    }

    public void boundsCameraByPoints(List<Point> pointList,float top,float left,float bottom,float right) {
        CameraOptions options = mapboxMap.cameraForCoordinates(pointList, new EdgeInsets(top, left, bottom, right), 0.0, 0.0);
        mapboxMap.setCamera(options);
    }

    public void boundsCameraByPolyline(Polygon polygon) {
        float padding = Dp2pxUtils.dip2px(context, 50);
        CameraOptions cameraPosition = mapboxMap.cameraForGeometry(polygon, new EdgeInsets(padding, padding, padding, padding), 0.0, 0.0);
        mapboxMap.setCamera(cameraPosition);
    }

    public PointAnnotation addBikeMarker(Point point, EbikeGis ebikeGis) {
        Bitmap bitmap = ImageUtils.getBitmap(R.mipmap.ic_bike_marker);
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withPoint(point).withIconImage(bitmap).withIconAnchor(IconAnchor.BOTTOM);
        PointAnnotation pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions);
        pointAnnotation.setData(new Gson().toJsonTree(ebikeGis));
        return pointAnnotation;
    }

    public void refreshBikeMarkers() {
        clearViewAnnotations();
        Bitmap bitmap = ImageUtils.getBitmap(R.mipmap.ic_bike_marker);
        for (PointAnnotation annotation : pointAnnotationManager.getAnnotations()) {
            annotation.setIconImageBitmap(bitmap);
            annotation.setSelected(false);
            pointAnnotationManager.update(annotation);
        }
    }

    public void changeSelectedMarkerImg(PointAnnotation point) {
        Bitmap bitmap = ImageUtils.getBitmap(R.mipmap.ic_marker_selected);
        point.setIconImageBitmap(bitmap);
        pointAnnotationManager.update(point);
    }

    public PointAnnotation addLineMarker(Point point, int resId) {
        Bitmap bitmap = ImageUtils.getBitmap(resId);
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withPoint(point).withIconImage(bitmap).withIconAnchor(IconAnchor.BOTTOM);
        return pointAnnotationManager.create(pointAnnotationOptions);
    }

    public PointAnnotation addTraceMarker(Point point, int resId) {
        Bitmap bitmap = ImageUtils.getBitmap(resId);
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withPoint(point).withIconImage(bitmap);
        return pointAnnotationManager.create(pointAnnotationOptions);
    }

    public void addPointClickListener() {
        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
            @Override
            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                refreshBikeMarkers();
                if (!pointAnnotation.isSelected()) {
                    pointAnnotation.setSelected(true);
                    moveCameraTo(pointAnnotation.getPoint());
                    changeSelectedMarkerImg(pointAnnotation);
                    createViewAnnotation(pointAnnotation);
                } else {
                    pointAnnotation.setSelected(false);
                    clearViewAnnotations();
                }
                return true;
            }
        });
    }

    public interface PointOclick {
        void onClick(PointAnnotation pointAnnotation);
    }

    public void clearMarker(PointAnnotation pointAnnotation) {
        pointAnnotationManager.delete(pointAnnotation);
    }

    public void clearMarkers() {
        pointAnnotationManager.deleteAll();
    }

    public PolylineAnnotation createPolyline(List<Point> points) {
        PolylineAnnotationOptions options = new PolylineAnnotationOptions();
        options.withPoints(points)
                .withLineColor(context.getColor(R.color.green))
                .withLineWidth(4.0)
                .withLineJoin(LineJoin.ROUND);
        return polylineAnnotationManager.create(options);
    }

    public void refreshPolyline(Point point) {
        List<PolylineAnnotation> list = polylineAnnotationManager.getAnnotations();
        list.get(0).getPoints().add(point);
        polylineAnnotationManager.update(list.get(0));
    }

    public void deleteAllPolylines() {
        polylineAnnotationManager.deleteAll();
    }

    public void createPolygon(List<Point> points) {
        PolygonAnnotationOptions options = new PolygonAnnotationOptions();
        options.withPoints(Collections.singletonList(points))
                .withFillColor(Color.rgb(255, 201, 218))
                .withFillOpacity(0.05);
        polygonAnnotationManager.create(options);
    }

    public void deleteAllPolygons() {
        polygonAnnotationManager.deleteAll();
    }


    /**
     * 退出时销毁
     */
    public void onDestroy() {
//        if (locationPlugin != null) {
//            locationPlugin.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener);
//            locationPlugin.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener);
//        }
//        if (gesturesPlugin != null) {
//            gesturesPlugin.removeOnMoveListener(onMoveListener);
//        }
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    public interface LocationChanged{
        void onChanged(Point point);
    }
}
