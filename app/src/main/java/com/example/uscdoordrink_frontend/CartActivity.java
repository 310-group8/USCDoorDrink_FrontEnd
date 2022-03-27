package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.adaptor.CartAdapter;
import com.example.uscdoordrink_frontend.entity.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class CartActivity extends AppCompatActivity {

        RecyclerView recyclerView;
        public TextView textViewPrice;
        Button buttonOrder;
        double subtotal = 0;
        double discount = 0;

        CartAdapter cartAdapter;
        List<Order> orders = new ArrayList<>();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cart);

            textViewPrice = findViewById(R.id.order_price);
            buttonOrder = findViewById(R.id.btnPlaceOrder);

            recyclerView = findViewById(R.id.recycler_cart);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

            loadCart();

            buttonOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textViewPrice.getText().toString().equals("$0.00"))
                        Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                    else
                        sendData();
                }
            });
        }


            //Helper Methods
            private void loadCart () {
                orders = Constants.currentUser.getCurrentOrder();
                cartAdapter = new CartAdapter(this, orders) {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        Toast.makeText(CartActivity.this, orders.get(position).getDrink(), Toast.LENGTH_SHORT).show();
                    }
                };
                recyclerView.setAdapter(cartAdapter);

                //Calculating total price
                for (Order order : orders) {
                    subtotal += order.getOrderPrice() * order.getQuantity();
                    discount += order.getDiscount() * order.getQuantity();
                }
                textViewPrice.setText(String.format(" $%s", subtotal));
            }

            private void sendData(){
                Intent i = new Intent(CartActivity.this, OrderActivity.class);
                i.putExtra(OrderActivity.SUBTOTAL, subtotal);
                i.putExtra(OrderActivity.DISCOUNTS, discount);
                startActivity(i);
                finish();
            }
        }
