package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.service.OrderNotificationService;
import com.example.uscdoordrink_frontend.service.OrderService;
import com.example.uscdoordrink_frontend.service.UserService;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {
    int progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        SeekBar s = (SeekBar) findViewById(R.id.seekBar);
        TextView textView = (TextView) findViewById(R.id.status_display);
        TextView username = (TextView) findViewById(R.id.a);
        TextView contactInfo = (TextView) findViewById(R.id.b);
        TextView address = (TextView) findViewById(R.id.c);
        Button b = findViewById(R.id.complete_order);
        Button apply = findViewById(R.id.apply_change);
        Request req = Constants.currentRequest;
        OrderService orderService = new OrderService();
        UserService userService = new UserService();
        progress = 0;

        username.setText(req.getName());
        contactInfo.setText(req.getContactInformation());
        address.setText(req.getAddress());

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
                switch(progress){
                    case 0:
                        textView.setText("Order created");
                        break;
                    case 1:
                        textView.setText("Start delivering");
                        break;
                    case 2:
                        textView.setText("Delivered");
                        break;
                    case 3:
                        textView.setText("Order Error");
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OrderManagementActivity.this, "Return to map.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(OrderManagementActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progress == 2) {
                    Instant end = Instant.now();
                    String endS = end.toString();
                    req.setEnd(endS);
                    req.setStatus(String.valueOf(progress));

                    // update local variable
                    // current request, add request to current user order history
                    Constants.currentRequest = null;
                    Constants.currentUser.setCurrentOrder(new ArrayList<Order>());
                    Constants.currentUser.addOrderToHistory(req);

                    // update firestore data: request and user order history
                    orderService.updateRequest(req);
                    userService.addUserRequest(Constants.currentUser.getUserName(), req);
                    Toast.makeText(OrderManagementActivity.this, "----- Order completed -----", Toast.LENGTH_SHORT).show();
                }else{
                    req.setStatus(String.valueOf(progress));
                    Constants.currentRequest = req;
                    orderService.updateRequest(req);
                    Toast.makeText(OrderManagementActivity.this, "Order status successfully changed!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}