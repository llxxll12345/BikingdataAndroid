package com.example.example;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    // map view
    private MapView myMapView;
    private BaiduMap myMap;

    private LocationClient myLocationClient = null;
    private boolean isFirstLocation = true;
    private MyLocationListener myListener = new MyLocationListener();

    private double latitude;
    private double longitude;

    int counter;
    Button mAdd, mMinus, mLocate;
    TextView mTotal, tLocate;

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //获取定位结果
            location.getTime();             //获取定位时间
            location.getLocationID();       //获取定位唯一ID，v7.2版本新增，用于排查定位问题
            location.getLocType();          //获取定位类型
            location.getLatitude();         //获取纬度信息
            location.getLongitude();        //获取经度信息
            location.getRadius();           //获取定位精准度
            location.getAddrStr();          //获取地址信息
            location.getCountry();          //获取国家信息
            location.getCountryCode();      //获取国家码
            location.getCity();             //获取城市信息
            location.getCityCode();         //获取城市码
            location.getDistrict();         //获取区县信息
            location.getStreet();           //获取街道信息
            location.getStreetNumber();     //获取街道码
            location.getLocationDescribe(); //获取当前位置描述信息
            location.getPoiList();          //获取当前位置周边POI信息

            location.getBuildingID();       //室内精准定位下，获取楼宇ID
            location.getBuildingName();     //室内精准定位下，获取楼宇名称
            location.getFloor();            //室内精准定位下，获取当前位置所处的楼层信息

            latitude = location.getLatitude();
            longitude = location.getLongitude();


            if (isFirstLocation) {
                isFirstLocation = false;
                setPosToCenter(myMap, location, true);
                tLocate.setText("tLocation: " + latitude + " " + longitude);
            }
        }
    }

    public void setPosToCenter(BaiduMap map, BDLocation bdLocation, Boolean isShowLoc) {
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(bdLocation.getRadius())
                .direction(bdLocation.getRadius()).latitude(bdLocation.getLatitude())
                .longitude(bdLocation.getLongitude()).build();
        map.setMyLocationData(locData);

        if (isShowLoc) {
            LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    public void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span = 1000;
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        option.setEnableSimulateGps(false);
        myLocationClient.setLocOption(option);
    }

    public void initMap() {
        LatLng GEO_BEIJING = new LatLng(39.945, 116.404);
        LatLng GEO_SHANGHAI = new LatLng(31.227, 121.481);

        MapStatusUpdate initLocStatus = MapStatusUpdateFactory.newLatLng(GEO_BEIJING);
        myMapView = (MapView) findViewById(R.id.bmapView);

        myMap = myMapView.getMap();
        //myMap.setMapStatus(initLocStatus);

        //baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        //baiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        myMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        myMap.setTrafficEnabled(true);
        //myMapView.showZoomControls(false);

        myMap.setMyLocationEnabled(true);

        myLocationClient = new LocationClient(getApplicationContext());
        myLocationClient.registerLocationListener(myListener);

        initLocation();

        myLocationClient.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        myMapView = (MapView) findViewById(R.id.bmapView);*/

        //Setting the options
       /* BaiduMapOptions options = new BaiduMapOptions();
        options.mapType(BaiduMap.MAP_TYPE_SATELLITE);

        //Without setting the layout
        myMapView = new MapView(this, options);
        setContentView(myMapView);

        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(18.0f);
        myMapView.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        */

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initMap();
        } else {
            MainActivityPermissionsDispatcher.ApplySuccessWithCheck(this);
        }

        counter = 0;
        mAdd = (Button) findViewById(R.id.bAdd);
        mMinus = (Button) findViewById(R.id.bMinus);
        mTotal = (TextView) findViewById(R.id.tResult);

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                mTotal.setText("Total: " + counter);
            }
        });

        mMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter--;
                mTotal.setText("Total: " + counter);
            }
        });

        mLocate = (Button) findViewById(R.id.blocate);
        tLocate = (TextView) findViewById(R.id.tLocation);

        mLocate.setOnClickListener(
            new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   LatLng ll = new LatLng(latitude, longitude);
                   MapStatus.Builder builder = new MapStatus.Builder();
                   builder.target(ll).zoom(18.0f);
                   myMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                   tLocate.setText("Location: " + latitude + " " + longitude);
               }
           }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        myMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    /**
     * 申请权限成功时
     */
    @NeedsPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    void ApplySuccess() {
        initMap();
    }

    /**
     * 申请权限告诉用户原因时
     * @param request
     */
    @OnShowRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    void showRationaleForMap(PermissionRequest request) {
        showRationaleDialog("使用此功能需要打开定位的权限", request);
    }

    /**
     * 申请权限被拒绝时
     *
     */
    @OnPermissionDenied(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapDenied() {
        Toast.makeText(this,"你拒绝了权限，该功能不可用",Toast.LENGTH_LONG).show();
    }

    /**
     * 申请权限被拒绝并勾选不再提醒时
     */
    @OnNeverAskAgain(Manifest.permission.ACCESS_COARSE_LOCATION)
    void onMapNeverAskAgain() {
        AskForPermission();
    }

    /**
     * 告知用户具体需要权限的原因
     * @param messageResId
     * @param request
     */
    private void showRationaleDialog(String messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();//请求权限
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }

    /**
     * 被拒绝并且不再提醒,提示用户去设置界面重新打开权限
     */
    private void AskForPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("当前应用缺少定位权限,请去设置界面打开\n打开之后按两次返回键可回到该应用哦");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName())); // 根据包名打开对应的设置界面
                startActivity(intent);
            }
        });
        builder.create().show();
    }
}
