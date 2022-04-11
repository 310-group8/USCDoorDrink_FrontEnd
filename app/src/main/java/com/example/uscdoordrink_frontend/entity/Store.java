package com.example.uscdoordrink_frontend.entity;


import android.util.Pair;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/18 2:14
 */


public class Store {

    private String storeUID;

    private String storeName;

    private HashMap<String, Double> storeAddress;

    private String addressString;

    private List<Drink> menu = new ArrayList<>();

    private String hashLocation;

    public String getStoreName(){return storeName;}

    public void setStoreName(String newName){storeName = newName;}

    public String getStoreUID(){return storeUID;}

    public void setStoreUID(String newUID){storeUID = newUID;}


    //(lat, lng)
    public LatLng getStoreAddress() {
        if (storeAddress != null){
            return new LatLng(Objects.requireNonNull(storeAddress.get("latitude")), Objects.requireNonNull(storeAddress.get("longitude")));
        }else{
            return null;
        }
    }

    public void setStoreAddress(Double latitude, Double longitude) {
        if (this.storeAddress == null){
            this.storeAddress = new HashMap<>();
        }
        this.storeAddress.put("latitude", latitude);
        this.storeAddress.put("longitude", longitude);
        hashLocation = GeoFireUtils.getGeoHashForLocation(new GeoLocation(Objects.requireNonNull(storeAddress.get("latitude")), Objects.requireNonNull(storeAddress.get("longitude"))));
    }

    public String getHashLocation(){
        return hashLocation;
    }

    public List<Drink> getMenu() {
        return menu;
    }

    public void setMenu(List<Drink> menu) {
        this.menu = menu;
    }

    private void setHashLocation(String hashLocation) {
        this.hashLocation = hashLocation;
    }

    public void setAddressString(String newAddressString){addressString = newAddressString;}

    public String getAddressString(){return addressString;}

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (!(o instanceof Store)){
            return false;
        }
        Store s = (Store) o;
        return Objects.equals(this.storeUID, s.getStoreUID()) &&
                Objects.equals(this.storeName, s.getStoreName()) &&
                Objects.equals(this.getStoreAddress(), s.getStoreAddress()) &&
                Objects.equals(this.addressString, s.getAddressString()) &&
                menu.equals(s.getMenu()) &&
                Objects.equals(this.hashLocation, s.getHashLocation());
    }

}
