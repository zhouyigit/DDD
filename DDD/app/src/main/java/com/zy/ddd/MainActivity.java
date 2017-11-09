package com.zy.ddd;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import android.view.animation.Animation;
import android.widget.TextView;

import com.amap.api.services.poisearch.PoiSearch;
import com.zy.login.LoginActivity;
import com.zy.tools.Tools;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    private MapView mMapView = null;
    private AMap aMap;
    //地图自带控件
    private UiSettings mUiSettings;
    //仅一次定位到屏幕中心
    protected boolean locationOnce = true;
    //
    PoiSearch.Query query;
    PoiSearch poiSearch;
    //
    ArrayList<Marker> markers;
    //底部详情
    RelativeLayout bottomDetail;
    TextView textView;
    Button buttonAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mMapView == null) {
            mMapView = (MapView) findViewById(R.id.map);
            //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
            mMapView.onCreate(savedInstanceState);
        }

        if (aMap == null) {
            aMap = mMapView.getMap();
            MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {
                    System.out.println("onMapLoaded");
                }
            });

            aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    LatLng target = cameraPosition.target;
                    System.out.println("onCameraChange");
                }

                @Override
                public void onCameraChangeFinish(CameraPosition cameraPosition) {
                    LatLng target = cameraPosition.target;
                    System.out.println("onCameraChangeFinish");
                    pinAroundCameraTarget(target);
                }
            });

            aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    System.out.println("onMyLocationChange");
                    if (locationOnce) {
                        locationOnce = false;
                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(location.getLatitude(),location.getLongitude()),16,0,0));
                        aMap.animateCamera(mCameraUpdate);
                        aMap.setOnMyLocationChangeListener(null);
                    }

                }
            });

            aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    System.out.println("onMarkerClick");
//                    if (marker.isInfoWindowShown()) {
//                        marker.hideInfoWindow();
//                    } else {
//                        marker.showInfoWindow();
//                    }
                    showDetailOfMarker(marker);
                    return true;// 返回 true 则表示接口已响应事件，否则返回false
                }
            });

            aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
//                    System.out.println("onMapClick");
                    for (Marker marker : markers) {
                        marker.hideInfoWindow();
                    }
                    hideDetailOfMarker(null);
                }
            });


        }

        if (mUiSettings == null) {
            mUiSettings = aMap.getUiSettings();
            int position = mUiSettings.getZoomPosition();
//            mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
            mUiSettings.setZoomControlsEnabled(false);
        }

        if (bottomDetail == null) {
            bottomDetail = (RelativeLayout) findViewById(R.id.bottom_introduce);
            textView = (TextView) findViewById(R.id.bottom_textView);
            buttonAppointment = (Button) findViewById(R.id.bottom_appointment);
            buttonAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("appointment clicked.");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
//                    overridePendingTransition(R.anim.anim_enter_from_bottom, R.anim.anim_static);
                }
            });
        }

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void showDetailOfMarker(Marker marker) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(bottomDetail.getHeight(), Tools.pxFromDp(100, this));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                bottomDetail.getLayoutParams().height = h;
                bottomDetail.requestLayout();
            }
        });
        valueAnimator.setDuration(50);
        valueAnimator.start();

    }

    public void hideDetailOfMarker(Marker marker) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(bottomDetail.getHeight(), Tools.pxFromDp(0, this));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                bottomDetail.getLayoutParams().height = h;
                bottomDetail.requestLayout();
            }
        });
        valueAnimator.setDuration(50);
        valueAnimator.start();
    }

    /**
     *  @function:
     *  @description:   在目标位置周围设置大头针
     *  @param:         目标位置
     *  @return:
     *  @history:
     *   date:          2017/11/8 14:01
     *   author:        yiyahanyu
     *   modification:
     */
    public void pinAroundCameraTarget(LatLng target) {

        System.out.println(target.latitude);
        System.out.println(target.longitude);

        ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();

        LatLng top = new LatLng(target.latitude+0.001, target.longitude);
        MarkerOptions topOptions = new MarkerOptions().position(top).title("top");
        markerOptionses.add(topOptions);

        LatLng left = new LatLng(target.latitude, target.longitude-0.001);
        MarkerOptions leftOptions = new MarkerOptions().position(left).title("left");
        markerOptionses.add(leftOptions);

        LatLng bottom = new LatLng(target.latitude-0.001, target.longitude);
        MarkerOptions bottomOptions = new MarkerOptions().position(bottom).title("bottom");
        markerOptionses.add(bottomOptions);

        LatLng right = new LatLng(target.latitude, target.longitude+0.001);
        MarkerOptions rightOptions = new MarkerOptions().position(right).title("right");
        markerOptionses.add(rightOptions);

        if (markers != null && markers.size() > 0) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }
        markers = aMap.addMarkers(markerOptionses, false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
