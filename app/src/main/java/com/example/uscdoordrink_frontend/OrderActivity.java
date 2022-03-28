package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.OrderNotificationService;
import com.example.uscdoordrink_frontend.service.OrderService;
import com.example.uscdoordrink_frontend.service.UserService;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.time.Instant;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    public double subtotal;
    public double discounts;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "UserService";

    Button order;
    TextView priceSub, pricedc, pricetax, priceSt, priceTotal;
    String address = null;
    double total;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        User u = new User("Wade", "Lyz007354", "3233560454", UserType.CUSTOMER);
        List<Order> orders = new ArrayList<>();
        Order a = new Order("Pineapple Lemonade", "7PqA0Yca8mKTntrvIHPT", 1, 5, 0);
        orders.add(a);
        u.setCurrentOrder(orders);
        Constants.currentUser = u;

        subtotal = getIntent().getDoubleExtra("subtotal", 0.0);
        discounts = getIntent().getDoubleExtra("discounts", 0.0);

        order = findViewById(R.id.place_order);
        priceSub = findViewById(R.id.pricesub);
        pricedc = findViewById(R.id.pricedc);
        pricetax = findViewById(R.id.pricetax);
        priceSt = findViewById(R.id.priceSt);
        priceTotal = findViewById(R.id.pricetotal);

        double tax = subtotal * 0.1;
        double deliverFee = 1.99;

        total = subtotal - discounts + tax + deliverFee;

        if(Constants.currentUser == null){
            Toast.makeText(OrderActivity.this, "Please login before placing an order.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(OrderActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
        priceSub.setText(String.valueOf(subtotal));
        pricedc.setText(String.valueOf(discounts));
        pricetax.setText(String.valueOf(tax));
        priceSt.setText(String.valueOf(deliverFee));
        priceTotal.setText(String.valueOf(total));

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showDialog();
            }

        });
    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("One more step!");
        builder.setMessage("Enter your Address: ");
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(layoutParams);
        builder.setView(editText);
//        builder.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(Constants.currentUser.getDailyCaffineConsume() >= 5){
                    Toast.makeText(OrderActivity.this, "Quote from USDA: Currently, strong evidence shows that consumption " +
                            "of coffee within the moderate range (3 to 5 cups per day or up to 400 mg/d caffeine) " +
                            "is not associated with increased long-term health risks among healthy individuals.", Toast.LENGTH_SHORT).show();

                }

                Instant instant = Instant.now();
                String start = instant.toString();

                Request req = new Request(start, Constants.currentUser.getUserName(),
                        Constants.currentUser.getContactInformation(),
                        address,
                        Constants.currentUser.getCurrentOrder().get(0).getStoreUID(),
                        total,
                        Constants.currentUser.getCurrentOrder());

                //sending request to firebase

                OrderService s = new OrderService();
                s.addRequest(req, new OnSuccessCallBack<Void>() {
                    @Override
                    public void onSuccess(Void input) {
                        Constants.currentUser.setCurrentOrder(new ArrayList<Order>());
                        Constants.currentRequest = req;
                        //request will only be added to user order history when completed
                        Toast.makeText(OrderActivity.this, "Order is placed. Thank You!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(OrderActivity.this, ViewOrderActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, new OnFailureCallBack<Exception>() {
                    @Override
                    public void onFailure(Exception input) {
                        Toast.makeText(getApplicationContext(),
                                "order failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }


}