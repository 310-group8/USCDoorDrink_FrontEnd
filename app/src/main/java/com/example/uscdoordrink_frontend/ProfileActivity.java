package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.service.OrderNotificationService;

public class ProfileActivity extends AppCompatActivity {
    Button btProfile, bOrder;
    TextView username;
    TextView contactInfo;
    TextView password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = findViewById(R.id.username);
        contactInfo = findViewById(R.id.contactInfo);
        password = findViewById(R.id.Password);
        username.setText(Constants.currentUser.getUserName());
        contactInfo.setText(Constants.currentUser.getContactInformation());
        password.setText(Constants.currentUser.getPassword());

        btProfile = findViewById(R.id.btn_ViewChart);
        btProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, ViewChartActivity.class);
                startActivity(i);
                finish();
            }
        });
        bOrder = findViewById(R.id.manage_order);
        bOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                if (Constants.currentUser.getUserType() == UserType.SELLER) {
                    i = new Intent(ProfileActivity.this, OrderManagementActivity.class);
                } else {
                    i = new Intent(ProfileActivity.this, ViewOrderActivity.class);
                }
                startActivity(i);
                finish();
            }
        });

        // start order Listening Service
        if (Constants.currentUser.getUserType() == UserType.CUSTOMER) {
            Intent service = new Intent(ProfileActivity.this, OrderNotificationService.class);
            service.putExtra("path", Constants.currentUser.getUserName());
            startService(service);
        } else {
            if (!Constants.currentUser.getStoreUID().equals("toBeAssigned")) {
                Intent service = new Intent(ProfileActivity.this, OrderNotificationService.class);
                service.putExtra("path", Constants.currentUser.getStoreUID());
                startService(service);
            }
        }
    }
}