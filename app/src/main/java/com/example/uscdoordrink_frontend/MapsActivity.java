package com.example.uscdoordrink_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.entity.Store;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.DirectionService;
import com.example.uscdoordrink_frontend.service.StoreService;
import com.example.uscdoordrink_frontend.utils.DirectionHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//import com.example.uscdoordrink_frontend.databinding.ActivityMapsBinding;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    //    private ActivityMapsBinding binding;
    private static String TAG = "MapsActivity";
    private static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static float DEFAULT_ZOOM = 14.0f;
    private static LatLng defaultLocation = new LatLng(34.022415, -118.285530);
    private static final String KEY_LOCATION = "location";
    private boolean locationPermissionGranted = false;
    private Location lastKnownLocation;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private List<Store> storesNearby;
    private Polyline mPolyline;
    private boolean mBound = false;
    private DirectionService mService;
    private List<Marker> markers;

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private FloatingActionButton fab_main, fab_profile, fab_cart, fab_recommendation, fab_order, fab_login;
    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        super.onCreate(savedInstanceState);

//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .add(R.id.map_container_view, mapFragment)
//                .commit();

        // Construct a PlacesClient
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        }
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        setUpButtons();
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, DirectionService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DirectionService.DirectionServiceBinder binder = (DirectionService.DirectionServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    public void onAddButtonClicked(){
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }

    private void setUpButtons(){
        fab_main = findViewById(R.id.fab_main);
        fab_cart = findViewById(R.id.fab_cart);
        fab_login = findViewById(R.id.fab_login);
        fab_order = findViewById(R.id.fab_order);
        fab_profile = findViewById(R.id.fab_profile);
        fab_recommendation = findViewById(R.id.fab_recommendation);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Fab is pressed successfully");
                onAddButtonClicked();
            }
        });

        fab_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "cart is pressed successfully");
                if(Constants.currentUser == null){
                    Toast.makeText(MapsActivity.this, "Please login first.",Toast.LENGTH_SHORT).show();
                }else {
                    Intent i;
                    if (Constants.currentUser.getUserType() == UserType.CUSTOMER) {
                        i = new Intent(MapsActivity.this, CartActivity.class);
                    } else {
                        i = new Intent(MapsActivity.this, ViewMenuActivity.class);
                    }
                    startActivity(i);
                    finish();
                }
            }
        });

        fab_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "login is pressed successfully");
                Intent i = new Intent(MapsActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        fab_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "order is pressed successfully");
                if(Constants.currentUser == null){
                    Toast.makeText(MapsActivity.this, "Please login to view or manage your order.",Toast.LENGTH_SHORT).show();
                }else {
                    Intent i;
                    if (Constants.currentUser.getUserType() == UserType.CUSTOMER) {
                        i = new Intent(MapsActivity.this, ViewOrderActivity.class);
                    } else {
                        i = new Intent(MapsActivity.this, OrderManagementActivity.class);
                    }
                    startActivity(i);
                    finish();
                }
            }
        });

        fab_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "profile is pressed successfully");
                if(Constants.currentUser == null){
                    Toast.makeText(MapsActivity.this, "Please login to view or manage your profile.",Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(MapsActivity.this, ProfileActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        fab_recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "recommendation is pressed successfully");
                if(Constants.currentUser == null){
                    Toast.makeText(MapsActivity.this, "Please login to see recommendations.",Toast.LENGTH_SHORT).show();
                }else {
                    Intent i = new Intent(MapsActivity.this, RecommendationActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void setVisibility(Boolean clicked){
        if(!clicked){
            fab_recommendation.setVisibility(View.VISIBLE);
            fab_cart.setVisibility(View.VISIBLE);
            fab_order.setVisibility(View.VISIBLE);
            fab_profile.setVisibility(View.VISIBLE);
            fab_login.setVisibility(View.VISIBLE);
        } else{
            fab_recommendation.setVisibility(View.INVISIBLE);
            fab_cart.setVisibility(View.INVISIBLE);
            fab_order.setVisibility(View.INVISIBLE);
            fab_profile.setVisibility(View.INVISIBLE);
            fab_login.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(Boolean clicked){
        if (!clicked){
            fab_recommendation.startAnimation(fromBottom);
            fab_cart.startAnimation(fromBottom);
            fab_order.startAnimation(fromBottom);
            fab_profile.startAnimation(fromBottom);
            fab_login.startAnimation(fromBottom);
            fab_main.startAnimation(rotateOpen);
        } else{
            fab_recommendation.startAnimation(toBottom);
            fab_cart.startAnimation(toBottom);
            fab_order.startAnimation(toBottom);
            fab_profile.startAnimation(toBottom);
            fab_login.startAnimation(toBottom);
            fab_main.startAnimation(rotateClose);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                setUpMarkers();
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    private void setUpMarkers(){
        StoreService storeService = new StoreService();
        storeService.getNearbyStore(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                new OnSuccessCallBack<List<Store>>() {
                    @Override
                    public void onSuccess(List<Store> input) {
                        storesNearby = input;
                        for (Store store : storesNearby){
                            LatLng storePosition = new LatLng(store.getStoreAddress().latitude, store.getStoreAddress().longitude);
                            @NonNull Marker marker = Objects.requireNonNull(mMap.addMarker(new MarkerOptions().position(storePosition).title(store.getStoreName())));
                            marker.setTag(store);
                        }
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(@NonNull final Marker marker){

                                Log.d(TAG, "Yes you did it");
                                drawPoly(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                                        ((Store)marker.getTag()).getStoreAddress(),
                                        DirectionHelper.Modes.driving);
                                return false;
                            }
                        });
                    }
                },
                new OnFailureCallBack<Exception>() {
                    @Override
                    public void onFailure(Exception input) {
                        Toast.makeText(getApplicationContext(), "Failed to get nearby stores", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void drawPoly(LatLng origin, LatLng destination, DirectionHelper.Modes mode){
        if (mPolyline != null){
            mPolyline.remove();
        }
        String URL = DirectionHelper.setUpURL(origin, destination, mode);
        if (mBound){
            mService.getDirections(URL,
                    new OnSuccessCallBack<Response>() {
                        @Override
                        public void onSuccess(Response response) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        List<LatLng> result = new ArrayList<>();
                                        String time = DirectionHelper.decodePolyline(response, result);
                                        if (time == null){
                                            throw new NullPointerException();
                                        }
                                        mPolyline = mMap.addPolyline(new PolylineOptions().addAll(result));
                                    }catch (NullPointerException e){
                                        Toast.makeText(getApplicationContext(), "Failed to get directions", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    },
                    new OnFailureCallBack<Exception>() {
                        @Override
                        public void onFailure(Exception input) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Failed to parse directions", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }
    }

}