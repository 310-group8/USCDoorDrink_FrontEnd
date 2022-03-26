package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.uscdoordrink_frontend.fragments.AddStoreMenu;
import com.example.uscdoordrink_frontend.fragments.AddStoreName;

public class AddStoreActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, AddStoreName.class, null)
                    .commit();
        }

        Button testButton = (Button) findViewById(R.id.TestButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromNameToMenu();
            }
        });
    }

    public void fromNameToMenu(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, AddStoreMenu.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null) // name can be null
                .commit();
    }


}