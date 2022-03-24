package com.example.uscdoordrink_frontend.entity;

/**
 * @Author: Yuxiang Zhang
 * @Date: 2022/3/22 22:28
 */

import java.util.List;

public class Request {
    private String Date;
    private String Name;
    private String ContactInformation;
    private String Address;
    private String StoreUID;
    private double Total;
    private String Status;
    private List<Order> orders;


    public Request(String date, String name, String contactInformation, String address, String UID, double total, List<Order> orders) {
        Date = date;
        Name = name;
        ContactInformation = contactInformation;
        Address = address;
        Total = total;
        Status = "0";   //0 is default, 1 is shipping, 2 is shipped
        this.orders = orders;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getContactInformation() {
        return ContactInformation;
    }

    public void setPhone(String contactInformation) {
        ContactInformation = contactInformation;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStoreUID() {
        return StoreUID;
    }

    public void setStoreUID(String storeUID) {
        StoreUID = storeUID;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setContactInformation(String contactInformation) {
        ContactInformation = contactInformation;
    }
}