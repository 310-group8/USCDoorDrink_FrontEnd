package com.example.uscdoordrink_frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.entity.Store;
import com.example.uscdoordrink_frontend.fragments.AddStoreMenu;
import com.example.uscdoordrink_frontend.fragments.AddStoreName;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.StoreService;
import com.example.uscdoordrink_frontend.viewmodels.AddStoreViewModel;
import com.google.android.libraries.places.api.Places;

public class AddStoreActivity extends AppCompatActivity {

    public AddStoreViewModel theStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
        }
        Intent intent = getIntent();
        String storeUID = intent.getStringExtra("storeUID");
        theStore = new ViewModelProvider(this).get(AddStoreViewModel.class);
        if (storeUID == null){
            theStore.mStoreModel.setValue(new Store());
        }else{
            StoreService storeService = new StoreService();
            storeService.getStoreByUID(storeUID,
                    new OnSuccessCallBack<Store>() {
                        @Override
                        public void onSuccess(Store input) {
                            theStore.mStoreModel.setValue(input);
                        }
                    },
                    new OnFailureCallBack<Exception>() {
                        @Override
                        public void onFailure(Exception input) {
                            Toast.makeText(getApplicationContext(),
                                    "cannot get your Store Information",
                                    Toast.LENGTH_SHORT).show();
                            theStore.mStoreModel.setValue(new Store());
                        }
                    });
        }
    }

    public void toMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

}