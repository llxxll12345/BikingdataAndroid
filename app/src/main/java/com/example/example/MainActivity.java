package com.example.example;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.objects.Points;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    private BitmapDescriptor markerIcon;

    int counter;
    Button mAdd, mMinus, mLocate;
    TextView mTotal, tLocate;

    // list of markups
    ArrayList<Points> markupList;

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();


            if (isFirstLocation) {
                isFirstLocation = false;
                setPosToCenter(myMap, location, true);
                tLocate.setText("tLocation Coords: " + latitude + " " + longitude + " City: " + location.getCity());
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //test
//        myMapView = (MapView) findViewById(R.id.bmapView);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initMap();
        } else {
            MainActivityPermissionsDispatcher.ApplySuccessWithCheck(this);
        }

        markupList = new ArrayList<>();

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


        myMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
               //Intent intent = new Intent();
                Bundle bundle = marker.getExtraInfo();
                int id = bundle.getInt("id");
                final String coords = bundle.getString("coord");
                final String name = bundle.getString("name");
                final String desp = bundle.getString("desp");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.marker);
                builder.setTitle("Markup detail");
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.marker_display, null);
                builder.setView(view);

                final TextView dCoord = (TextView) view.findViewById(R.id.dCoord);
                final TextView dName = (TextView) view.findViewById(R.id.dName);
                final TextView dDes = (TextView) view.findViewById(R.id.dDes);

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

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(R.drawable.marker);
                    builder.setTitle("Add a new markup");
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.marker_dialog, null);
                    builder.setView(view);

                    final EditText locname = (EditText)view.findViewById(R.id.locationName);
                    final EditText desp = (EditText)view.findViewById(R.id.description);
                    final TextView tCoords = (TextView)view.findViewById(R.id.tCoords);
                    tCoords.setText("Coords: " + curLat + ", " + curLong);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String a = locname.getText().toString().trim();
                            String b = desp.getText().toString().trim();
                            markupList.add(new Points(point, markupList.size(), a, b));

                            Bundle myBundle = new Bundle();
                            myBundle.putInt("id", markupList.size() - 1);
                            myBundle.putString("coord", curLat + ", " + curLong);
                            myBundle.putString("name", a);
                            myBundle.putString("desp", b);

                            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.marker_layout, null);
                            TextView locName = (TextView) view.findViewById(R.id.loc_name);
                            locName.setText(a);
                            markerIcon = BitmapDescriptorFactory.fromBitmap(Utility.getViewBitmap(view));

                            MarkerOptions options = new MarkerOptions()
                                    .position(point)
                                    .icon(markerIcon)
                                    .zIndex(markupList.size() - 1)
                                    .draggable(true)
                                    .extraInfo(myBundle);
                            myMap.addOverlay(options);

                            OverlayOptions textOption = new TextOptions()
                                    //                    .bgColor(0xAAFFFF00)
                                    .fontSize(16)
                                    .fontColor(Color.BLACK)
                                    .text("Point1")
                                    .position(point);

                            myMap.addOverlay(textOption);

                            Toast.makeText(MainActivity.this, "Added markupname: " + a, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }

                @Override
                public boolean onMapPoiClick(MapPoi mapPoi) {
                    return false;
                }
            }
        );

        Button switchButton =(Button)findViewById(R.id.bSwitch);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                //Intent intent =new Intent(MainActivity.this, ButtonSelectorActivity.class);
                //startActivityForResult(intent, 1);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.marker);
                builder.setTitle("Username and password");
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_up_dialog, null);
                builder.setView(view);

                final EditText username = (EditText)view.findViewById(R.id.username);
                final EditText password = (EditText)view.findViewById(R.id.password);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String a = username.getText().toString().trim();
                        String b = password.getText().toString().trim();
                        Toast.makeText(MainActivity.this, "Username: " + a + ", Password: " + b, Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
             }
        });
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

    @Override
    protected  void onActivityResult(int requestCode,int resultCode ,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 2){
            String content = data.getStringExtra("data");
            tLocate.setText("Returned.");
        }
    }
}
