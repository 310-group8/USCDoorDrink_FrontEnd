package com.example.uscdoordrink_frontend.entity;


import android.util.Pair;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.util.List;
import java.util.Map;


/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/18 2:14
 */


public class Store {

    private String storeUID;

    private Pair<Double, Double> storeAddress;

    private List<Drink> menu;

    private Map<Drink, Double> discounts;

    private String hashLocation;

    public String getStoreUID(){return storeUID;}

    public void setStoreUID(String newUID){storeUID = newUID;}

    //(lat, lng)
    public Pair<Double, Double> getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(Pair<Double, Double> storeAddress) {
        this.storeAddress = storeAddress;
        hashLocation = GeoFireUtils.getGeoHashForLocation(new GeoLocation(this.storeAddress.first, this.storeAddress.second));
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

    public Map<Drink, Double> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(Map<Drink, Double> discounts) {
        this.discounts = discounts;
    }
}
