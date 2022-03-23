package com.example.uscdoordrink_frontend.entity;


import android.util.Pair;

import java.util.List;
import java.util.Map;


/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/18 2:14
 */


public class Store {

    private Pair<Double, Double> storeAddress;

    private List<Drink> menu;

    private Map<Drink, Double> discounts;

    public Pair<Double, Double> getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(Pair<Double, Double> storeAddress) {
        this.storeAddress = storeAddress;
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
