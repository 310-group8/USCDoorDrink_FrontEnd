package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.service.OrderNotificationService;

import java.util.ArrayList;
import java.util.List;

public class ViewOrderActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        TextView display = findViewById(R.id.order_status_display);

        List<Order> orders = new ArrayList<>();
        Order a = new Order("Pineapple Lemonade", "7PqA0Yca8mKTntrvIHPT", 1, 5, 0);
        orders.add(a);
        User u = new User();
        u.setCurrentOrder(orders);
        Constants.currentUser = u;

        if(Constants.currentRequest == null){
            display.setText("You don't have a order currently.");
        }else{
            //start receiving order status changes
            startListen();
            display.setText(Constants.getOrderStatus(Constants.currentRequest.getStatus()));
        }
    }

    public void startListen(){
        Intent service = new Intent(ViewOrderActivity.this, OrderNotificationService.class);
        service.putExtra("path", Constants.currentUser.getUserName());
        startService(service);
    }
}