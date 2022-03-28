package com.example.uscdoordrink_frontend;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
//import com.example.uscdoordrink_frontend.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
//    private ActivityMapsBinding binding;
    private static String TAG = "MapsActivity";

    private Animation rotateOpen, rotateClose, fromBottom, toBottom;
    private FloatingActionButton fab_main, fab_profile, fab_cart, fab_recommendation, fab_order, fab_login;
    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                Intent i = new Intent(MapsActivity.this, CartActivity.class);
                startActivity(i);
                finish();
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
                Intent i = new Intent(MapsActivity.this, ViewOrderActivity.class);
                startActivity(i);
                finish();
            }
        });

        fab_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "profile is pressed successfully");
                Intent i = new Intent(MapsActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

        fab_recommendation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "recommendation is pressed successfully");
                Intent i = new Intent(MapsActivity.this, RecommendationActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void onAddButtonClicked(){
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
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
}