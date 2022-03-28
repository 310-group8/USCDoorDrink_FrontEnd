package com.example.uscdoordrink_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.adaptor.MenuAdapter;
import com.example.uscdoordrink_frontend.entity.Drink;
import com.example.uscdoordrink_frontend.entity.Menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;



public class ViewMenuActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MenuAdapter menuAdapter;
    ArrayList<String> drinkNameList;
    ArrayList<String> priceList;
    ArrayList<Drink> drinks;
    FirebaseFirestore db;
    String storeUID = "ENenoMqEZbpgVln7C4eN";
    Button btSelect;
    static final String TAG = "ViewMenuActivity";
    List<Drink> menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_menu);
        btSelect = findViewById(R.id.btn_AddToCart);
        db = FirebaseFirestore.getInstance();
        db.collection("Store")
                .document(storeUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task){
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            String drinkUID = document.get("drinkUID").toString();

                            db = FirebaseFirestore.getInstance();
                            db.collection("Drink")
                                    .document(drinkUID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task){
                                            if(task.isSuccessful()){
                                                DocumentSnapshot document = task.getResult();
                                                menu = document.toObject(Menu.class).drinks;
                                            }else{
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });

                            Log.d(TAG, "success");
                        }else{
                            Log.w(TAG, "get failed with ", task.getException());
                        }
                    }
                });
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        drinks = new ArrayList<Drink>();
        drinkNameList = new ArrayList<String>();
        priceList = new ArrayList<String>();
        menuAdapter = new MenuAdapter(this, drinks);
        recyclerView.setAdapter(menuAdapter);

        for(Drink drink: menu){
            drinkNameList.add(drink.getDrinkName());
            priceList.add(String.valueOf(drink.getPrice()));
        }

        menuAdapter.buttonSetOnclick(new MenuAdapter.ButtonInterface(){
            @Override
            public void onclick(View view, int position) {
                Toast.makeText(ViewMenuActivity.this, "Select"+position, Toast.LENGTH_SHORT).show();
                sendData(position);
            }
        });
    }

    public static <T> List<T> castList(Object obj, Class<T> clazz)
    {
        List<T> result = new ArrayList<T>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    private void sendData(int position){
        Intent i = new Intent(ViewMenuActivity.this, AddCartActivity.class);
        i.putExtra(AddCartActivity.INGREDIENTS, drinks.get(position).getIngredients().toString());
        i.putExtra(AddCartActivity.DRINKNAME, drinks.get(position).getDrinkName());
        i.putExtra(AddCartActivity.UID, drinks.get(position).getStoreUID());
        i.putExtra(AddCartActivity.QUANTITY, 1);
        i.putExtra(AddCartActivity.PRICE, drinks.get(position).getPrice());
        i.putExtra(AddCartActivity.DISCOUNT, 0);
        startActivity(i);
    }

    private void getMenu(String drinkUID){

    }
}