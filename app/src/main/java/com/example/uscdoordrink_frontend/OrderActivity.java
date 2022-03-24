package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.Request;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderActivity extends AppCompatActivity {

    public static final String SUBTOTAL = "SUBTOTAL";
    public static final String DISCOUNTS = "DISCOUNTS";


    //init firebase
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference request = firebaseDatabase.getReference("Request");
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

                address = editText.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String d = formatter.format(date);

                Request req = new Request(d, Constants.currentUser.getUserName(),
                        Constants.currentUser.getContactInformation(),
                        address,
                        total,
                        Constants.currentUser.getCurrentOrder());

                //sending to firebase
                request.child(String.valueOf(System.currentTimeMillis())).setValue(req);

                Constants.currentUser.setCurrentOrder(new ArrayList<Order>());
                Constants.currentUser.addOrderToHistory(req);
                Toast.makeText(OrderActivity.this, "Order is placed. Thank You!", Toast.LENGTH_SHORT).show();
                finish();
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