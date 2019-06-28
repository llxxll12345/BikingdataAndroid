package com.example.example;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.Point;
import com.bumptech.glide.Glide;
import com.example.fragment.SlideMenu;
import com.example.objects.Points;
import com.example.utility.DensityUtil;
import com.example.utility.FileUtility;
import com.example.utility.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

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
    private LatLng latLng;

    private BitmapDescriptor markerIcon;


    final int[] photoIdsDisplay = {    R.id.imageView13
                                        , R.id.imageView14
                                        , R.id.imageView15
                                        , R.id.imageView16
                                        , R.id.imageView17
                                        , R.id.imageView18 };

    FloatingActionButton btnLocate, btnAddPt, btnRoute;
    TextView tLocate, txApp;

    boolean recordingRoute = false;

    ArrayList<String> tempPhotoUrlList;
    // list of markups
    ArrayList<Points> markupList;

    ArrayList<LatLng> routeList;

    /* For sliding menu */
    private Context mContext;

    private int mScreenWidth = 0;
    // private SlideMenu mSlideViewLeft;
    private SlideMenu mSlideViewRight;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("Location: ", latitude + ", " + longitude);

            if (recordingRoute) {
                routeList.add(new LatLng(latitude, longitude));
                txApp.setText("New location: " + latitude + ", " + longitude);
            }

            if (isFirstLocation) {
                isFirstLocation = false;
                setPosToCenter(myMap, location, true);
                tLocate.setText("坐标:" + String.format("%.2f", latitude) + ", " +
                        String.format("%.2f", longitude) + "\n城市: " + location.getCity());

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
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
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

        myMapView.removeViewAt(1);

        myMap.setMyLocationEnabled(true);

        myLocationClient = new LocationClient(getApplicationContext());
        myLocationClient.registerLocationListener(myListener);

        initLocation();

        myLocationClient.start();
    }

    public void initMarkUpList() {
        // Get from local
        String text = FileUtility.readFilefromLocal(this, "markup.txt");
        /*if (text != null) {
            markupList = FileUtility.decodeData(text);
            int id = 0;
            for (Points markup: markupList) {
                String paths = markup.getPhotoPath();
                addMarkerToMap(markup.getLocationName()
                        , markup.getDescription()
                        , markup.getLongitude()
                        , markup.getLongitude()
                        , paths
                        , id++
                        );
            }
        } else {*/
        markupList = new ArrayList<>();
        //}
        // Get from server
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initMap();

        //markupList = new ArrayList<>();
        initMarkUpList();

        //mLocate = (Button) findViewById(R.id.blocate);
        btnRoute  = (FloatingActionButton) findViewById(R.id.flbtn_route);
        btnLocate = (FloatingActionButton) findViewById(R.id.flbtn_locate);
        btnAddPt  = (FloatingActionButton) findViewById(R.id.flbtn_addpt);
        tLocate = (TextView) findViewById(R.id.tLocation);
        txApp = (TextView) findViewById(R.id.tx_app);



        btnLocate.setOnClickListener(
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


        btnRoute.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recordingRoute) {
                        recordingRoute = false;
                        routeList.add(new LatLng(latitude, longitude));
                        OverlayOptions ooPolyline = new PolylineOptions()
                                .width(10)
                                .color(Integer.valueOf(Color.BLUE))
                                .points(routeList);
                        myMap.addOverlay(ooPolyline);
                        txApp.setText("Recording Route Ends");
                    } else {
                        recordingRoute = true;
                        txApp.setText("Recording Route");
                        routeList = new ArrayList<>();
                    }
                }
            }
        );

        btnAddPt.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final double curLat = latLng.latitude;
                    final double curLong = latLng.longitude;

                    System.out.println("Hit Latitude= " + curLat + " Longitude= " + curLong);
                    // clear the map layer
                    // myMap.clear();
                    final LatLng point = new LatLng(curLat, curLong);


                    Intent intent =new Intent(MainActivity.this, MarkerActivity.class);
                    intent.putExtra("lat", String.format("%5.2f", curLat));
                    intent.putExtra("long", String.format("%5.2f", curLong));
                    startActivityForResult(intent, 1);
                }
            }
        );


        myMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
            }
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                latLng = mapStatus.target;
            }
        });


        myMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               //Intent intent = new Intent();
                Bundle bundle = marker.getExtraInfo();
                int id = bundle.getInt("id");
                final String coords = bundle.getString("coord");
                final String name   = bundle.getString("name");
                final String desp   = bundle.getString("desp");
                final String paths  = bundle.getString("paths");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.marker);
                builder.setTitle("Markup detail");
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.marker_display, null);
                builder.setView(view);

                final TextView dCoord = (TextView) view.findViewById(R.id.dCoord);
                final TextView dName = (TextView) view.findViewById(R.id.dName);
                final TextView dDes = (TextView) view.findViewById(R.id.dDes);

                String[] pathList = paths.split(";");
                for (int i = 0; i < pathList.length; i++) {
                    if (pathList[i] == null) continue;
                    Log.d("path", i + " " + pathList[i]);
                    ImageView photoView= view.findViewById(photoIdsDisplay[i]);
                    Glide.with(MainActivity.this)
                            .load(pathList[i])
                            .into(photoView);
                }

                dCoord.setText(coords);
                dName.setText(name);
                dDes.setText(desp);

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                //intent.putExtra("id", id);
                //Toast.makeText(MainActivity.this, "marker id: " + id, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        myMap.setOnMapClickListener(
            new BaiduMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    final double curLat = latLng.latitude;
                    final double curLong = latLng.longitude;

                    System.out.println("Hit Latitude= " + curLat + " Longitude= " + curLong);
                    // clear the map layer
                    // myMap.clear();
                    final LatLng point = new LatLng(curLat, curLong);


                    Intent intent =new Intent(MainActivity.this, MarkerActivity.class);
                    intent.putExtra("lat", String.format("%5.2f", curLat));
                    intent.putExtra("long", String.format("%5.2f", curLong));
                    startActivityForResult(intent, 1);
                }
                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            }
        );

        /*Button switchButton =(Button)findViewById(R.id.bSwitch);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this, UploadActivity.class);
                startActivityForResult(intent, 1);
             }
        });*/

        tempPhotoUrlList = new ArrayList<>();

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner_func);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String result = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("BikingData");
        setSupportActionBar(toolbar);

        initSlideMenu();
    }


    private void initSlideMenu() {
        mContext = this;
        mScreenWidth = DensityUtil.getScreenWidthAndHeight(mContext)[0];
        mSlideViewRight = SlideMenu.create(this, SlideMenu.Positon.RIGHT);
        View menuViewRight = LayoutInflater.from(mContext).inflate(R.layout.user_drawer,null);
        mSlideViewRight.setMenuView(MainActivity.this, menuViewRight);
        mSlideViewRight.setMenuWidth(mScreenWidth * 7 / 9);
        Button right = (Button)findViewById(R.id.btn_drawer_open);
        right.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!mSlideViewRight.isShow())
                    mSlideViewRight.show();
            }
        });
        menuViewRight.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSlideViewRight.isShow()) {
                    mSlideViewRight.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(mSlideViewRight.isShow()){
            mSlideViewRight.dismiss();
            return ;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myMapView.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("App", " paused");
        super.onPause();
        myMapView.onPause();    // Be careful !!!!
    }

    @Override
    protected void onDestroy() {
        Log.i("App", " destroyed");
        myMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.btn_settings:
                Toast.makeText(this, "设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_search:
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_about:
                Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
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
     *      Successfully acquired permission to access coarse location
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
     *      Denided -> ask the user to activate permissions in settings
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

    void addMarkerToMap(String locName, String description, String longitude, String latitude, String paths, int id) {
        Bundle myBundle = new Bundle();
        myBundle.putInt("id",id);
        myBundle.putString("coord", latitude + ", " + longitude);
        myBundle.putString("name", locName);
        myBundle.putString("desp", description);
        myBundle.putString("paths", paths);

        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.marker_layout, null);
        TextView locNameText = (TextView) view.findViewById(R.id.loc_name);
        locNameText.setText(locName);
        markerIcon = BitmapDescriptorFactory.fromBitmap(Utility.getViewBitmap(view));

        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
        MarkerOptions options = new MarkerOptions()
                .position(point)
                .icon(markerIcon)
                .zIndex(id)
                .draggable(true)
                .extraInfo(myBundle);
        myMap.addOverlay(options);

        OverlayOptions textOption = new TextOptions()
                //                    .bgColor(0xAAFFFF00)
                .fontSize(16)
                .fontColor(Color.BLACK)
                .text(locName)
                .position(point);
        myMap.addOverlay(textOption);
    }

    @Override
    protected  void onActivityResult(int requestCode, int resultCode ,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("App", "result!!! " + requestCode + " " + resultCode);
        if(requestCode == 1 && resultCode == 2){
            //String content = data.getStringExtra("data");
            String locName = data.getStringExtra("location");
            String description  = data.getStringExtra("description");
            String longitude    = data.getStringExtra("long");
            String latitude     = data.getStringExtra("lat");
            String paths        = data.getStringExtra("paths");
            markupList.add(new Points(latitude, longitude, markupList.size(), locName, description));
            FileUtility.saveFiletoLocal(this, "markup.txt", FileUtility.formatData(markupList));

            addMarkerToMap(locName, description, longitude, latitude, paths, markupList.size() - 1);

            Toast.makeText(MainActivity.this, "Added markup Name: " + locName, Toast.LENGTH_SHORT).show();
            tLocate.setText("Returned.");
        } else if (requestCode == 1 && requestCode == 3) {

        }
    }
}
