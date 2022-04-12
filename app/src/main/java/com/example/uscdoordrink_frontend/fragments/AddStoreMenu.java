package com.example.uscdoordrink_frontend.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.AddStoreActivity;
import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.R;
import com.example.uscdoordrink_frontend.entity.Drink;
import com.example.uscdoordrink_frontend.entity.Store;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.StoreService;
import com.example.uscdoordrink_frontend.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddStoreMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStoreMenu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddStoreMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddStoreMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static AddStoreMenu newInstance(String param1, String param2) {
        AddStoreMenu fragment = new AddStoreMenu();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_add_store_menu, container, false);
        @NonNull Store theStore = Objects.requireNonNull(((AddStoreActivity) requireActivity()).theStore.mStoreModel.getValue());
        ListView listView = (ListView) mainView.findViewById(R.id.menu_list_view);
        List<String> drinkNames = new ArrayList<>();
        for (Drink drink : theStore.getMenu()){
            drinkNames.add(drink.getDrinkName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, drinkNames);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String drinkName = adapter.getItem(i);
                Bundle drinkData = new Bundle();
                for (Drink d : theStore.getMenu()){
                    if (Objects.equals(d.getDrinkName(), drinkName)){
                        drinkData.putString("storeUID", d.getStoreUID());
                        drinkData.putString("drinkName", d.getDrinkName());
                        drinkData.putDouble("discount", d.getDiscount());
                        drinkData.putStringArrayList("ingredients", (ArrayList<String>) d.getIngredients());
                        drinkData.putDouble("price", d.getPrice());
                        break;
                    }
                }
                Navigation.findNavController(view).navigate(R.id.action_menu_to_drink, drinkData);
            }
        });


        Button addButton = (Button) mainView.findViewById(R.id.button_add_item);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_menu_to_drink);
            }
        });

        Button confirmButton = (Button) mainView.findViewById(R.id.button_confirm_menu);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (theStore.getMenu().isEmpty()){
                    Toast.makeText(getContext(), "Menu cannot be empty", Toast.LENGTH_SHORT).show();
                }else{
                    StoreService storeService = new StoreService();
                    if (theStore.getStoreUID() == null){
                        storeService.addStore(theStore,
                                new OnSuccessCallBack<Void>() {
                                    @Override
                                    public void onSuccess(Void input) {

                                        // add storeUID to user
                                        Constants.currentUser.setStoreUID(theStore.getStoreUID());
                                        UserService service = new UserService();
                                        service.updateStoreUID(Constants.currentUser.getUserName(), theStore.getStoreUID());

                                        // start order listening service

                                        Navigation.findNavController(view).navigate(R.id.action_menu_to_successful);
                                    }
                                },
                                new OnFailureCallBack<Exception>() {
                                    @Override
                                    public void onFailure(Exception input) {
                                        Toast.makeText(getContext(), "failed to upload your store", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }else{
                        storeService.updateStore(theStore,
                                new OnSuccessCallBack<Void>() {
                                    @Override
                                    public void onSuccess(Void input) {
                                        Navigation.findNavController(view).navigate(R.id.action_menu_to_successful);
                                    }
                                },
                                new OnFailureCallBack<Exception>() {
                                    @Override
                                    public void onFailure(Exception input) {
                                        Toast.makeText(getContext(), "failed to upload your store", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
        return mainView;
    }
}