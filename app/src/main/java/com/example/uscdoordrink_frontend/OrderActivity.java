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
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.OrderNotificationService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.time.Instant;

public class OrderActivity extends AppCompatActivity {

    public static final String SUBTOTAL = "SUBTOTAL";
    public static final String DISCOUNTS = "DISCOUNTS";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "UserService";

    Button order;
    String address = null;
    double tax = Double.parseDouble(SUBTOTAL) * 0.1;
    double deliverFee = 1.99;
    double total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        total = Double.parseDouble(SUBTOTAL) - Double.parseDouble(DISCOUNTS) + tax + deliverFee;

        if(Constants.currentUser == null){
            Toast.makeText(OrderActivity.this, "Please login before placing an order.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(OrderActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
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
                address = editText.getText().toString();

                Instant start = Instant.now();
                String UID = Constants.currentUser.getCurrentOrder().get(0).getStoreUID();

                Request req = new Request(start, Constants.currentUser.getUserName(),
                        Constants.currentUser.getContactInformation(),
                        address,
                        UID,
                        total,
                        Constants.currentUser.getCurrentOrder());

                //sending request to firebase
                OrderNotificationService s = new OrderNotificationService();
                s.addRequest(req, new OnSuccessCallBack<Void>() {
                    @Override
                    public void onSuccess(Void input) {
                        Constants.currentUser.setCurrentOrder(new ArrayList<Order>());
                        Constants.currentUser.addOrderToHistory(req);
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