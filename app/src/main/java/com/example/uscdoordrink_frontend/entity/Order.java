package com.example.uscdoordrink_frontend.entity;

import android.util.Pair;

import java.util.Date;

/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/18 2:15
 */

/**
 * @Author: Yuxiang Zhang
 * @Date: 2022/3/20 18:24
 */


public class Order {

    private Date orderTime;

    private String storeUID;

    private double orderPrice;

    private Pair<Double, Double> deliveryAddress;

    private Drink drink;

    public Order(Date time, Drink drk, String store, Double price, Pair<Double, Double> deliverAddress) {
        orderTime = time;
        storeUID = store;
        orderPrice = price;
        deliveryAddress = deliverAddress;
    }

    // getters and setters
    public Date getOrderTime() {
        return orderTime;
    }

    public String getStoreUID() {
        return storeUID;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public Pair<Double, Double> getDeliveryAddress() {
        return deliveryAddress;
    }

    public Drink getDrink() {
        return drink;
    }


    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public void setStoreUID(String storeUID) {
        this.storeUID = storeUID;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setDeliveryAddress(Pair<Double, Double> deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }
}

