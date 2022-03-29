package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.service.OrderNotificationService;
import com.example.uscdoordrink_frontend.service.UserService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewOrderActivity extends AppCompatActivity {
    Button complete, return_to_map;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        TextView display = findViewById(R.id.order_status_display);

        complete = findViewById(R.id.complete);
        return_to_map = findViewById(R.id.return_to_map);

        if (Constants.currentRequest == null) {
            display.setText("You don't have a order currently.");
        } else {
            display.setText(Constants.getOrderStatus(Constants.currentRequest.getStatus()));
            if (Constants.currentRequest.getStatus().equals("2")) {
                complete.setVisibility(View.VISIBLE);
            } else{
                complete.setVisibility(View.INVISIBLE);
            }
        }

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update local and online current request, order history, user order history
                display.setText(Constants.getOrderStatus("You don't have a order currently."));
                Constants.currentUser.addOrderToHistory(Constants.currentRequest);
                UserService service = new UserService();
                service.addUserRequest(Constants.currentUser.getUserName(), Constants.currentRequest);
                deleteRequest();
                Constants.currentRequest = null;
                Toast.makeText(ViewOrderActivity.this, "Order completed.", Toast.LENGTH_SHORT).show();
            }
        });

        return_to_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewOrderActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void deleteRequest(){
        Request r = Constants.currentRequest;
        final DocumentReference cusRef = db.collection("Request").document(r.getName());
        final DocumentReference selRef = db.collection("Request").document(r.getStoreUID());
        cusRef.delete();
        selRef.delete();
    }



}