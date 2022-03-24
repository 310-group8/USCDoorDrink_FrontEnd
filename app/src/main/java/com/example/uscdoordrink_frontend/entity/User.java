package com.example.uscdoordrink_frontend.entity;


import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/18 2:13
 */


public class User {

    private String userName;

    private String contactInformation;

    private String password;

    private List<Order> currentOrder;

    private List<Request> orderHistory;

    private UserType userType;

    private String storeUID;

    public User(String userName, String password, UserType u) {
        this.userName = userName;
        this.contactInformation = contactInformation;
        this.password = password;
        if(u == userType.CUSTOMER){
            this.userType = userType.CUSTOMER;
        } else {
            this.userType = userType.SELLER;
        }
        currentOrder = new ArrayList<Order>();
        orderHistory = new ArrayList<Request>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Order> getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(List<Order> currentOrder) {
        this.currentOrder = currentOrder;
    }

    public List<Request> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Request> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getStoreUID() {
        return storeUID;
    }

    public void setStoreUID(String storeUID) {
        this.storeUID = storeUID;
    }

    public void addOrderToHistory(Request r){
        orderHistory.add(r);
    }
}