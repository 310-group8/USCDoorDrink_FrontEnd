package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Order;

public class AddCartActivity extends AppCompatActivity {
    public static final String INGREDIENTS = "Ingredients";
    public static final String DRINKNAME = "Drink Name";
    public static final String UID = "UID";
    public static final String QUANTITY = "Quantity";
    public static final String PRICE = "Price";
    public static final String DISCOUNT = "Discount";
    Button btAdd;
    TextView ingredients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cart);
        ingredients = findViewById(R.id.ingredients);
        Intent intent = getIntent();
        ingredients.setText(intent.getStringExtra(INGREDIENTS));
        btAdd = findViewById(R.id.btn_AddToCart);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.currentUser.getCurrentOrder().add(new Order(intent.getStringExtra(DRINKNAME),
                        intent.getStringExtra(UID),
                        Integer.parseInt(intent.getStringExtra(QUANTITY)),
                        Double.parseDouble(intent.getStringExtra(PRICE)),
                        0));
                Intent i = new Intent(AddCartActivity.this, CartActivity.class);
                startActivity(i);
            }
        });
    }
}