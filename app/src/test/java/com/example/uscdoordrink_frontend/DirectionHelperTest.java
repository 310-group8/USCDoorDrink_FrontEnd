package com.example.uscdoordrink_frontend;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.uscdoordrink_frontend.utils.DirectionHelper;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/27 17:04
 */
public class DirectionHelperTest {

    @Test
    public void showResponse() {
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/directions/json?origin=34.022415,-118.285530&destination=34.1381,-118.3534&mode=driving&key=AIzaSyCoj0HiPuANRmMbBN4fA51oCiRa8y_q8fA")
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            List<LatLng> result = new ArrayList<>();
            String time = DirectionHelper.decodePolyline(response, result);
            System.out.println(time);
            System.out.println(result);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}
