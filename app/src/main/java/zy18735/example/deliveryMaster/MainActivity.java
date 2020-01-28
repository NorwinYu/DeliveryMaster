package zy18735.example.deliveryMaster;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import java.util.ArrayList;
import java.util.List;

import zy18735.example.deliveryMaster.dal.provider.MyProvider;
import zy18735.example.deliveryMaster.dal.provider.MyProviderContract;
import zy18735.example.deliveryMaster.location.amap.util.SensorEventHelper;
import zy18735.example.deliveryMaster.model.Order;
import zy18735.example.deliveryMaster.model.User;
import zy18735.example.deliveryMaster.util.image.BitmapUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationSource, AMapLocationListener, RouteSearch.OnRouteSearchListener {

    /**
     * Amap location
     */

    private MapView mapView;
    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private SensorEventHelper mSensorHelper;
    private Circle mCircle;

    private UiSettings mUiSettings;

    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    private static final int STROKE_WIDTH = 5;

    private static final float GPS_LOCKED_SCALE_RATE = 0.5f;

    /**
     * Current
     */

    private Integer curUserId = 1;
    private LatLng curLatLng;
    private Integer curSelectedOrderId = 1;

    /**
     * Access Request Code
     */

    // request code for location access
    static final int ACCESS_FINE_LOCATION = 0;

    /**
     * DEFAULT VALUES
     */

    private static final double DEFAULT_START_CENTER_LATITUDE = 29.82681;
    private static final double DEFAULT_START_CENTER_LONGITUDE = 121.546337;
    private static final int DEFAULT_ZOON = 11;

    private static final float DEFAULT_START_CIRCLE_R = 10000;

    /**
     * User Status
     */

    private static final int STATUS_REST = 0;
    private static final int STATUS_DELIVERING = 1;

    /**
     * Order Status
     */

    //0. haven't be chosen 1. price successfully set 2. pending pickup 3. picked-up 4. delivered
    private static final int FOOD_STATUS_HAVENT_CHOSEN = 0;
    private static final int FOOD_STATUS_PRICE_SET = 1;
    private static final int FOOD_STATUS_PENDING_PICKUP = 2;
    private static final int FOOD_STATUS_PICKED_UP = 3;
    private static final int FOOD_STATUS_DELIVERED = 4;

    SimpleCursorAdapter dataAdapter;

    private boolean demoOrdersFirstFix = false;

    /**
     * Request Code - Activity
     */

    static final int ACTIVITY_REQUEST_SET_PRICE = 0;    // request code for set price


    /**
     * Amap Search
     */

    // search
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private final int ROUTE_TYPE_DRIVE = 2;
    private ProgressDialog progDialog = null;
    private Context mContext;

    /**
     * Save current values
     */

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
        outState.putInt("curSelectedOrderId", curSelectedOrderId);
        outState.putInt("curUserId", curUserId);
    }

    /**
     * A lot of stuff to start
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            curSelectedOrderId = savedInstanceState.getInt("curSelectedOrderId");
            curUserId = savedInstanceState.getInt("curUserId");
        }

        // main layout
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Further Function: Message With Food Factory", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set user info
        initUserInfo();

        // set cur default
        curLatLng = new LatLng(DEFAULT_START_CENTER_LATITUDE, DEFAULT_START_CENTER_LONGITUDE);

        // set default orders
        SharedPreferences sharedPreferences = this.getSharedPreferences("save", MODE_PRIVATE);
        demoOrdersFirstFix = sharedPreferences.getBoolean("demoOrdersFirstFix",demoOrdersFirstFix);
        initDemoOrders();

        // set selected
        refreshSelected();

        // map
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // check location permission
        checkLocationPermission();

        // search
        mContext = this.getApplicationContext();

    }

    /**
     * Life circle
     */

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("demoOrdersFirstFix", demoOrdersFirstFix);
        editor.commit();

        super.onStop();
    }

    /**
     * ------------------- Flowing functions related to NavigationView -------------------
     */

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_main) {

        } else if (id == R.id.nav_statistics) {
            Intent intent = new Intent(MainActivity.this, MyStatisticsActivity.class);
            intent.putExtra("order",getCurUser().getOrder_number());
            intent.putExtra("distance",getCurUser().getDistance_sum());
            intent.putExtra("money",getCurUser().getPrice_sum());
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * ------------------- Flowing functions related to Location Permission Checking -------------------
     */

    public void checkLocationPermission() {
        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // no permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        }
        else {
            initAfterPermissionsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ACCESS_FINE_LOCATION:
                if(grantResults.length >0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    initAfterPermissionsGranted();
                }else{
                    Toast.makeText(MainActivity.this, "\"GPS\" location provider requires location permission. Please ACCEPT the location permission.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * ------------------- Flowing functions related to UI Init -------------------
     */

    public void initAfterPermissionsGranted() {

        // init map
        initMap();

        // load orders
        refreshOrders();
    }

    /**
     * Init Map
     */

    public void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        // set language
        aMap.setMapLanguage(AMap.ENGLISH);

        // set location
        aMap.setLocationSource(this);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);

        // location style
        setupLocationStyle();

        // get ui settings
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);

        // set zoom
        LatLng location = new LatLng(DEFAULT_START_CENTER_LATITUDE, DEFAULT_START_CENTER_LONGITUDE);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOON));

        // search
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }

    /**
     * Init User Info: Name, Email, Status
     */

    public void initUserInfo() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        // set name
        TextView textViewName = hView.findViewById(R.id.userName);
        textViewName.setText(getCurUser().getName());

        // set email
        TextView textViewEmail = hView.findViewById(R.id.userEmail);
        textViewEmail.setText(getCurUser().getEmail());

        // set status
        updateStatusText();
    }

    public void updateStatusText() {
        TextView textViewStatus = (TextView) findViewById(R.id.statusTextView);
        if (getCurUser().getStatus().equals(STATUS_REST)) {
            textViewStatus.setText(R.string.rest);
        }
        else if (getCurUser().getStatus().equals(STATUS_DELIVERING)) {
            textViewStatus.setText(R.string.delivering);
        }
        else {

        }
    }

    /**
     * User mode encapsulation, get current user by curUserId
     */

    public User getCurUser() {

        // check user id
        if (curUserId < 0) {
            return null;
        }

        User user = new User();

        String[] projection = new String[] {
                MyProviderContract._ID,
                MyProviderContract.USER_NAME,
                MyProviderContract.USER_EMAIL,
//                MyProviderContract.USER_PASS,
                MyProviderContract.USER_TYPE,
                MyProviderContract.USER_ORDER_NUMBER,
                MyProviderContract.USER_PRICE_SUM,
                MyProviderContract.USER_DISTANCE_SUM,
                MyProviderContract.USER_STATUS,
                MyProviderContract.USER_CUR_ORDER_ID
        };

        Cursor cursor = getContentResolver().query(Uri.parse(MyProviderContract.USER_URI.toString()+"/"+curUserId.toString()), projection, null, null, null);

        // check result count - should be only one user
        if (cursor != null) {
            if (cursor.getCount() == 1) {

                cursor.moveToFirst();

                if (cursor.getColumnIndex(MyProviderContract._ID) >= 0)
                    user.set_id(cursor.getInt(cursor.getColumnIndex(MyProviderContract._ID)));

                if (cursor.getColumnIndex(MyProviderContract.USER_NAME) >= 0)
                    user.setName(cursor.getString(cursor.getColumnIndex(MyProviderContract.USER_NAME)));

                if (cursor.getColumnIndex(MyProviderContract.USER_EMAIL) >= 0)
                    user.setEmail(cursor.getString(cursor.getColumnIndex(MyProviderContract.USER_EMAIL)));

                if (cursor.getColumnIndex(MyProviderContract.USER_TYPE) >= 0)
                    user.setType(cursor.getInt(cursor.getColumnIndex(MyProviderContract.USER_TYPE)));

                if (cursor.getColumnIndex(MyProviderContract.USER_ORDER_NUMBER) >= 0)
                    user.setOrder_number(cursor.getInt(cursor.getColumnIndex(MyProviderContract.USER_ORDER_NUMBER)));

                if (cursor.getColumnIndex(MyProviderContract.USER_PRICE_SUM) >= 0)
                    user.setPrice_sum(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.USER_PRICE_SUM)));

                if (cursor.getColumnIndex(MyProviderContract.USER_DISTANCE_SUM) >= 0)
                    user.setDistance_sum(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.USER_DISTANCE_SUM)));

                if (cursor.getColumnIndex(MyProviderContract.USER_STATUS) >= 0)
                    user.setStatus(cursor.getInt(cursor.getColumnIndex(MyProviderContract.USER_STATUS)));

                if (cursor.getColumnIndex(MyProviderContract.USER_CUR_ORDER_ID) >= 0)
                    user.setCur_order_id(cursor.getInt(cursor.getColumnIndex(MyProviderContract.USER_CUR_ORDER_ID)));
            }
            else {
                Log.e("getCurU", "Get Current User Error, curId - "+curUserId+" found users count - "+ cursor.getCount());
                return null;
            }
        }
        else {
            Log.e("getCurU", "Get Current User Error, curId - "+curUserId+", Reason: null");
            return null;
        }

        return user;
    }

    /**
     * ------------------- Flowing functions related to Amap Location Service -------------------
     */

    /**
     * Activate Amap Location Serviece
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * Stop Amap Location Serviece
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private void setupLocationStyle(){

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // location icon
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        // location stroke color
        myLocationStyle.strokeColor(STROKE_COLOR);
        // location stroke width
        myLocationStyle.strokeWidth(STROKE_WIDTH);
        // location fill color
        myLocationStyle.radiusFillColor(FILL_COLOR);
        // set to map
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * recall function after location changed
     */

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                LatLng location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, DEFAULT_START_CIRCLE_R);
                    addMarker(location);
                    mSensorHelper.setCurrentMarker(mLocMarker);
                } else {
                    mCircle.setCenter(location);
                    mCircle.setRadius(DEFAULT_START_CIRCLE_R);
                    mLocMarker.setPosition(location);
                }
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, aMap.getCameraPosition().zoom));
                curLatLng = location;
                refreshOrders();
            } else {
                String errText = "Location fail," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    /**
     * Add circle
     */

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(5);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    /**
     * Add marker
     */

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(
                BitmapUtil.scaleBitmap(bMap, GPS_LOCKED_SCALE_RATE));

        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
    }

    /**
     * ------------------- Flowing functions related to Orders -------------------
     */

    public void updateOrders() {

        // projection
        String[] projection = new String[] {
                MyProviderContract._ID,
                MyProviderContract.ORDERS_TITLE,
                MyProviderContract.ORDERS_DETAIL,
                MyProviderContract.ORDERS_PICKUP_LATITUDE,
                MyProviderContract.ORDERS_PICKUP_LONGITUDE,
                MyProviderContract.ORDERS_DELIVERY_LATITUDE,
                MyProviderContract.ORDERS_DELIVERY_LONGITUDE,
                MyProviderContract.ORDERS_STATUS,
                MyProviderContract.ORDERS_DELIVERYMAN_ID,
                MyProviderContract.ORDERS_DELIVERYMAN_NAME,
                MyProviderContract.ORDERS_PRICE,
                MyProviderContract.ORDERS_DISTANCE
        };

        // set query
        Cursor cursor = getContentResolver().query(MyProviderContract.ORDERS_URI, projection, null, null, MyProviderContract.ORDERS_DISTANCE);

        if(cursor !=null && cursor.moveToFirst()){
            do{
                Order order = new Order(
                        null,
                        cursor.getString(cursor.getColumnIndex(MyProviderContract.ORDERS_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MyProviderContract.ORDERS_DETAIL)),
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LATITUDE)),
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LONGITUDE)),
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LATITUDE)),
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LONGITUDE)),
                        cursor.getInt(cursor.getColumnIndex(MyProviderContract.ORDERS_STATUS)),
                        cursor.getInt(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERYMAN_ID)),
                        cursor.getString(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERYMAN_NAME)),
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PRICE)),
                        calculateDistance(
                                cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LATITUDE)),
                                cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LONGITUDE)),
                                (float)curLatLng.latitude, (float)curLatLng.longitude)
                );

                getContentResolver().update(
                        Uri.parse(MyProviderContract.ORDERS_URI+"/"+cursor.getInt(cursor.getColumnIndex(MyProviderContract._ID))),
                        order.toContentValuesWithoutID(), null, null);
            }while(cursor.moveToNext());
        }
    }

    public void refreshOrders() {

        updateOrders();

        // projection
        String[] projection = new String[] {
                MyProviderContract._ID,
                MyProviderContract.ORDERS_TITLE,
                MyProviderContract.ORDERS_DETAIL,
                MyProviderContract.ORDERS_PICKUP_LATITUDE,
                MyProviderContract.ORDERS_PICKUP_LONGITUDE,
                MyProviderContract.ORDERS_DELIVERY_LATITUDE,
                MyProviderContract.ORDERS_DELIVERY_LONGITUDE,
                MyProviderContract.ORDERS_STATUS,
                MyProviderContract.ORDERS_DELIVERYMAN_ID,
                MyProviderContract.ORDERS_DELIVERYMAN_NAME,
                MyProviderContract.ORDERS_PRICE,
                MyProviderContract.ORDERS_DISTANCE
        };

        // cols to display on the screen
        String colsToDisplay [] = new String[] {
                MyProviderContract._ID,
                MyProviderContract.ORDERS_TITLE,
                MyProviderContract.ORDERS_DISTANCE,
                MyProviderContract.ORDERS_PRICE
        };

        // set ids of cols to display
        int[] colResIds = new int[] {
                R.id.value0,
                R.id.value1,
                R.id.value2,
                R.id.value3
        };

        // set query
        Cursor cursor = getContentResolver().query(MyProviderContract.ORDERS_URI, projection, null, null, MyProviderContract.ORDERS_DISTANCE);

        // set adapter
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.db_item_layout,
                cursor,
                colsToDisplay,
                colResIds,
                0);

        ListView listView = (ListView) findViewById(R.id.orderListView);
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // update selected order id
                        TextView textView0 = view.findViewById(R.id.value0);
                        curSelectedOrderId = stringToInteger(String.valueOf(textView0.getText()));

                        refreshSelected();
                    }
                }
        );

        addOrdersMarker(cursor);
    }

    /**
     * ------------------- Flowing functions related to selected Button -------------------
     */

    public void refreshSelected() {
        Order order = getOrderById(curSelectedOrderId);

        // set selected order title
        TextView textViewSelectedOrder = findViewById(R.id.selectedOrder);
        textViewSelectedOrder.setText(String.valueOf(order.getTitle()));

        TextView textViewStatus = findViewById(R.id.selectedStatus);
        switch (order.getStatus()) {
            case FOOD_STATUS_HAVENT_CHOSEN:
                textViewStatus.setText("haven't be chosen");
                setSelectedButtonSetPrice();
                break;
            case FOOD_STATUS_PRICE_SET:
                textViewStatus.setText("price successfully set");
                setSelectedButtonSelectOrder();
                break;
            case FOOD_STATUS_PENDING_PICKUP:
                textViewStatus.setText("pending pickup");
                setSelectedButtonPickedUp();
                break;
            case FOOD_STATUS_PICKED_UP:
                textViewStatus.setText("picked-up");
                setSelectedButtonDelivered();
                break;
            case FOOD_STATUS_DELIVERED:
                textViewStatus.setText("delivered");
                disableButton();
                break;
        }
    }

    public void setSelectedButtonSetPrice() {
        Button button = findViewById(R.id.selectedButton);
        button.setEnabled(true);
        button.setText("Set Price");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrice(v);
            }
        });
    }

    public void setSelectedButtonSelectOrder() {
        Button button = findViewById(R.id.selectedButton);
        button.setEnabled(true);
        button.setText("Select Order");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectOrder(v);
            }
        });
    }

    public void setSelectedButtonPickedUp() {
        Button button = findViewById(R.id.selectedButton);
        button.setEnabled(true);
        button.setText("Picked Up");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickedUp(v);
            }
        });
    }

    public void setSelectedButtonDelivered() {
        Button button = findViewById(R.id.selectedButton);
        button.setEnabled(true);
        button.setText("Delivered");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delivered(v);
            }
        });
    }

    public void disableButton() {
        Button button = findViewById(R.id.selectedButton);
        button.setOnClickListener(null);
        button.setEnabled(false);
        button.setText("Button Disabled");
    }

    public void setPrice(View view) {
        Intent intent = new Intent(this, SetPriceActivity.class);
        Order order = getOrderById(curSelectedOrderId);
        intent.putExtra("oldPrice", order.getPrice().intValue());

        // start set price activity and listen for result
        startActivityForResult(intent,ACTIVITY_REQUEST_SET_PRICE);
    }

    public void selectOrder(View view) {

        // update order info
        Order order = getOrderById(curSelectedOrderId);
        order.setStatus(FOOD_STATUS_PENDING_PICKUP);
        order.setDeliveryman_name(getCurUser().getName());
        order.setDeliveryman_id(getCurUser().get_id());
        getContentResolver().update(
                Uri.parse(MyProviderContract.ORDERS_URI+"/"+curSelectedOrderId),
                order.toContentValuesWithDeliverMan(), null, null);

        // update user
        User user = getCurUser();
        user.setStatus(STATUS_DELIVERING);
        user.setCur_order_id(curSelectedOrderId);
        getContentResolver().update(
                Uri.parse(MyProviderContract.USER_URI+"/"+curUserId),
                user.toContentValuesStatusAndOrder(), null, null);

        // refresh info
        refreshOrders();
        refreshSelected();
        initUserInfo();

        // change map zoom
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, DEFAULT_ZOON+2));

        // route
        searchRoute(curLatLng, new LatLng(order.getPickup_latitude(), order.getPickup_longitude()));
    }

    public void pickedUp(View view) {

        // update order info
        Order order = getOrderById(curSelectedOrderId);
        order.setStatus(FOOD_STATUS_PICKED_UP);
        getContentResolver().update(
                Uri.parse(MyProviderContract.ORDERS_URI+"/"+curSelectedOrderId),
                order.toContentValuesWithDeliverMan(), null, null);

        // refresh info
        refreshOrders();
        refreshSelected();

        // route
        searchRoute(new LatLng(order.getPickup_latitude(), order.getPickup_longitude()),
                new LatLng(order.getDelivery_latitude(), order.getDelivery_longitude()));
    }

    public void delivered(View view) {

        // update order info
        Order order = getOrderById(curSelectedOrderId);
        order.setStatus(FOOD_STATUS_DELIVERED);
        getContentResolver().update(
                Uri.parse(MyProviderContract.ORDERS_URI+"/"+curSelectedOrderId),
                order.toContentValuesWithDeliverMan(), null, null);

        // update user
        User user = getCurUser();
        user.setStatus(STATUS_REST);
        user.setCur_order_id(-1);
        user.setPrice_sum(user.getPrice_sum()+order.getPrice());
        user.setOrder_number(user.getOrder_number()+1);
        user.setDistance_sum(user.getDistance_sum()+calculateDistance(
                    order.getPickup_latitude(), order.getPickup_longitude(),
                    order.getDelivery_latitude(), order.getDelivery_longitude()));
        getContentResolver().update(
                Uri.parse(MyProviderContract.USER_URI+"/"+curUserId),
                user.toContentValuesStatusDelivered(), null, null);

        // refresh info
        refreshOrders();
        refreshSelected();
        initUserInfo();

        broadcastIntent(user.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_REQUEST_SET_PRICE) {
            if(resultCode == RESULT_OK) {
                Order order = getOrderById(curSelectedOrderId);
                order.setPrice((float)stringToInteger(data.getStringExtra("price")));
                order.setStatus(FOOD_STATUS_PRICE_SET);
                getContentResolver().update(
                        Uri.parse(MyProviderContract.ORDERS_URI+"/"+curSelectedOrderId),
                        order.toContentValuesWithPrice(), null, null);

                // refresh info
                refreshOrders();
                refreshSelected();
            }
            else if (resultCode == RESULT_CANCELED)
                Log.d("SetP","set price - result canceled");
        }
    }

    /**
     * Translate string to integer
     *
     * return -1 if failed to translate
     *
     * @param     startInt  started string int
     * @return    Integer
     */

    public Integer stringToInteger(String startInt) {

        // set default
        int result = -1;

        try {
            result = Integer.parseInt(startInt);
        }catch (Exception e) {

        }
        return result;
    }

    public Order getOrderById(Integer id) {

        // projection
        String[] projection = new String[] {
                MyProviderContract._ID,
                MyProviderContract.ORDERS_TITLE,
                MyProviderContract.ORDERS_DETAIL,
                MyProviderContract.ORDERS_PICKUP_LATITUDE,
                MyProviderContract.ORDERS_PICKUP_LONGITUDE,
                MyProviderContract.ORDERS_DELIVERY_LATITUDE,
                MyProviderContract.ORDERS_DELIVERY_LONGITUDE,
                MyProviderContract.ORDERS_STATUS,
                MyProviderContract.ORDERS_DELIVERYMAN_ID,
                MyProviderContract.ORDERS_DELIVERYMAN_NAME,
                MyProviderContract.ORDERS_PRICE,
                MyProviderContract.ORDERS_DISTANCE
        };

        Cursor cursor = getContentResolver().query(Uri.parse(MyProviderContract.ORDERS_URI.toString()+"/"+id.toString()), projection, null, null, null);

        Order order = new Order();

        // check result count - should be only one user
        if (cursor != null) {
            if (cursor.getCount() == 1) {

                cursor.moveToFirst();

                if (cursor.getColumnIndex(MyProviderContract._ID) >= 0)
                    order.set_id(cursor.getInt(cursor.getColumnIndex(MyProviderContract._ID)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_TITLE) >= 0)
                    order.setTitle(cursor.getString(cursor.getColumnIndex(MyProviderContract.ORDERS_TITLE)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_DETAIL) >= 0)
                    order.setDetail(cursor.getString(cursor.getColumnIndex(MyProviderContract.ORDERS_DETAIL)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LATITUDE) >= 0)
                    order.setPickup_latitude(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LATITUDE)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LONGITUDE) >= 0)
                    order.setPickup_longitude(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LONGITUDE)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LATITUDE) >= 0)
                    order.setDelivery_latitude(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LATITUDE)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LONGITUDE) >= 0)
                    order.setDelivery_longitude(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LONGITUDE)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_STATUS) >= 0)
                    order.setStatus(cursor.getInt(cursor.getColumnIndex(MyProviderContract.ORDERS_STATUS)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERYMAN_ID) >= 0)
                    order.setDeliveryman_id(cursor.getInt(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERYMAN_ID)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERYMAN_NAME) >= 0)
                    order.setDeliveryman_name(cursor.getString(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERYMAN_NAME)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_PRICE) >= 0)
                    order.setPrice(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PRICE)));

                if (cursor.getColumnIndex(MyProviderContract.ORDERS_DISTANCE) >= 0)
                    order.setDistance(cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_DISTANCE)));
            }
            else {
                Log.e("getOrderById", "Get Order By Id Error, id - "+id+" found orders count - "+ cursor.getCount());
                return null;
            }
        }
        else {
            Log.e("getOrderById", "Get Order By Id Error, id - "+id+" Reason: null");
            return null;
        }

        return order;
    }

    public void addOrdersMarker(Cursor cursor) {
        if(cursor !=null && cursor.moveToFirst()){
            do{
                LatLng latLngPickUp = new LatLng(
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LATITUDE)),
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_PICKUP_LONGITUDE)));
                LatLng latLngDeliver = new LatLng(
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LATITUDE)),
                        cursor.getFloat(cursor.getColumnIndex(MyProviderContract.ORDERS_DELIVERY_LONGITUDE)));
                addOrderPickUpMarker(latLngPickUp);
                addOrderDeliverMarker(latLngDeliver,
                        cursor.getString(cursor.getColumnIndex(MyProviderContract.ORDERS_TITLE)));
                addLine(latLngPickUp, latLngDeliver);
            }while(cursor.moveToNext());
        }
    }

    public void addOrderPickUpMarker(LatLng latlng) {
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.start);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(
                BitmapUtil.scaleBitmap(bMap, GPS_LOCKED_SCALE_RATE));

        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        aMap.addMarker(options);
    }

    public void addOrderDeliverMarker(LatLng latlng, String title) {
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.end);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(
                BitmapUtil.scaleBitmap(bMap, GPS_LOCKED_SCALE_RATE));

        MarkerOptions options = new MarkerOptions();
        options.title(title);
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        aMap.addMarker(options);
    }

    public void addLine(LatLng latLng1, LatLng latLng2) {
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(latLng1);
        latLngs.add(latLng2);
        aMap.addPolyline(new PolylineOptions().addAll(latLngs)
                .width(5).color(R.color.blue));
    }

    /**
     * ------------------- Flowing functions related to Demo Orders Generation -------------------
     */

    /**
     * Add three demo orders, order1 is the path of the given gpx file: from university to dongqian lake
     */

    public void initDemoOrders() {
        if (!demoOrdersFirstFix) {
            demoOrdersFirstFix = true;
            addOrder("Order 1", "This is the demo order 1", (float)29.8009, (float)121.5610, (float)29.7475, (float)121.6390);
            addOrder("Order 2", "This is the demo order 2", (float)29.824427, (float)121.575176, (float)29.84453, (float)121.629078);
            addOrder("Order 3", "This is the demo order 3", (float)29.829193, (float)121.531064, (float)29.861577, (float)121.536814);
        }
    }

    /**
     * Add order function encapsulation
     */

    public void addOrder(String title, String detail, Float pickup_latitude, Float pickup_longitude, Float delivery_latitude, Float delivery_longitude) {
        Order order = new Order(
                null,
                title,
                detail,
                pickup_latitude,
                pickup_longitude,
                delivery_latitude,
                delivery_longitude,
                FOOD_STATUS_HAVENT_CHOSEN,
                null,
                null,
                (float)0.0,
                calculateDistance(pickup_latitude, pickup_longitude, (float)curLatLng.latitude, (float)curLatLng.longitude)
        );
        getContentResolver().insert(MyProviderContract.ORDERS_URI, order.toContentValues());
    }

    /**
     * AMapUtils is uesd to help calculate the distance
     */

    public float calculateDistance(Float latitude1, Float longitude1, Float latitude2, Float longitude2) {
        LatLng latLng1 = new LatLng(latitude1, longitude1);
        LatLng latLng2 = new LatLng(latitude2, longitude2);
        return AMapUtils.calculateLineDistance(latLng1,latLng2) / (float) 1000.0;
    }

    /**
     * ------------------- Flowing functions related to Amap Search -------------------
     */

    /**
     * Search function encapsulation
     */

    public void searchRoute(LatLng latLng1, LatLng latLng2) {
        mStartPoint = new LatLonPoint(latLng1.latitude, latLng1.longitude);
        mEndPoint = new LatLonPoint(latLng2.latitude, latLng2.longitude);
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
    }

    /**
     * Show dialog and start search - type = drive
     */

    public void searchRouteResult(int routeType, int mode) {
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);
        }
    }

    /**
     * Override bus route search result
     */

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {

    }

    /**
     * Override drive route search result
     */

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
//        aMap.clear();// clear map
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();

                } else if (result != null && result.getPaths() == null) {

                }

            } else {

            }
        } else {

        }

    }

    /**
     * Override walk route search result
     */

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {

    }

    /**
     * Override ride route search result
     */

    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {

    }

    /**
     * Show Progress
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("Searching path...");
        progDialog.show();
    }

    /**
     * ------------------- Flowing functions related to Broadcast -------------------
     */

    /**
     * Dismiss Progress
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * Send Broadcast with user name when finished one order
     */
    public void broadcastIntent(String name){
        Intent intent = new Intent();
        intent.putExtra("name", name);
        intent.setAction("zy18735.example.deliveryMaster.ORDER_FINISH_INTENT");
        sendBroadcast(intent);
    }
}
