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
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.service.OrderNotificationService;

import java.time.Instant;
import java.time.Duration;

public class OrderManagementActivity extends AppCompatActivity {

    SeekBar s = (SeekBar) findViewById(R.id.seekBar);
    TextView textView = (TextView) findViewById(R.id.status_display);
    TextView username = (TextView) findViewById(R.id.a);
    TextView contactInfo = (TextView) findViewById(R.id.b);
    TextView address = (TextView) findViewById(R.id.c);
    Button b = findViewById(R.id.complete_order);
    Request req = Constants.currentRequest;
    OrderNotificationService service = new OrderNotificationService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        username.setText(req.getName());
        contactInfo.setText(req.getContactInformation());
        address.setText(req.getAddress());

        s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch(i){
                    case 0:
                        textView.setText("Order created");
                        break;
                    case 1:
                        textView.setText("Start delivering");
                        break;
                    case 2:
                        textView.setText("Delivered, please click complete order button.");
                        Instant end = Instant.now();
                        req.setEnd(end);
                        service.updateRequest(req);
                        break;
                    case 3:
                        textView.setText("Order Error");
                        break;
                }
                req.setStatus(String.valueOf(i));
                service.updateRequest(req);
                Toast.makeText(OrderManagementActivity.this, "Order status successfully changed!", Toast.LENGTH_SHORT).show();
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
    }
}