package com.example.uscdoordrink_frontend.constants;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.entity.User;

public class Constants {
    public static User currentUser;
    public static Request currentRequest;
    public final static String UPDATE = "Update";
    public final static String DELETE = "Delete";
    public final static String USER_PHONE = "UserPhone";
    public final static String USER_PASSWORD = "UserPassword";
    public final static String USER_NAME = "UserName";
    public final static String CLIENT = "client";
    public final static String SERVER = "server";

    public static String getOrderStatus(String status) {
        switch (status) {
            case "0":
                return "Your order has placed.";
            case "1":
                return "Your order is delivering in progress.";
            case "2":
                return "Congratulations! Your order has delivered.";
            default:
                return "Sorry, your  order status is currently not available.";
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
