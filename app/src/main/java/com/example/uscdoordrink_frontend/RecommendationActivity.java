package com.example.uscdoordrink_frontend;

import android.content.Intent;
import android.os.Bundle;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.viewmodels.AddStoreViewModel;


import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.time.Instant;
import java.util.*;

public class RecommendationActivity extends AppCompatActivity {
    TextView recommendations;
    Button btReturn;
    Set hasViewed = new HashSet<>();
    List<Order> orderHistory = new ArrayList<>();
    Map<String, Integer> frequency = new TreeMap<>();
    List<Integer> frequencyList = new ArrayList<>();
    List<String> ItemList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation);
        btReturn = findViewById(R.id.btn_return);
        recommendations = findViewById(R.id.recommendations);
//        User user = new User("abc", "123", "456", UserType.CUSTOMER);
//        List<Order> orders = new ArrayList<>();
//        Order test = new Order("Milk Tea", "efg", 1, 20, 0);
//        orders.add(test);
//        List<Request> testList = new ArrayList<>();
//        Request testR = new Request(Instant.now(), "Wade", "123", "abc", "efg", 20, orders);
//        testList.add(testR);
//        Constants.currentUser = user;
//        user.setOrderHistory(testList);
        if(Constants.currentUser.getOrderHistory() == null){
            recommendations.setText("I am sorry. We can't tell which drink you favor because we don't have your order history record.");
        }else {
            for (Request request : Constants.currentUser.getOrderHistory()) {
                for (Order order : request.getOrders()) {
                    orderHistory.add(order);
                }
            }
            for (Order order : orderHistory) {
                if (!hasViewed.contains(order)) {
                    frequency.put(order.getDrink(), Collections.frequency(orderHistory, order));
                    hasViewed.add(order);
                }
            }
            frequency.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue());
            if(frequency.size() == 0){
                recommendations.setText("I am sorry. We can't tell which drink you favor because we don't have your order history record.");
            }else {
                for (int i = 0; i < 5; i++) {
                    if (frequency.keySet().toArray()[frequency.size() - i - 1].toString() != null) {
                        ItemList.add(frequency.keySet().toArray()[frequency.size() - i - 1].toString());
                        if(frequency.size() - i - 1 == 0){
                            break;
                        }
                    } else {
                        break;
                    }
                }
                recommendations.setText(ItemList.toString());
            }
        }
        btReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecommendationActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
    }
}