package com.example.uscdoordrink_frontend.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.uscdoordrink_frontend.AddStoreActivity;
import com.example.uscdoordrink_frontend.R;
import com.example.uscdoordrink_frontend.entity.Drink;
import com.example.uscdoordrink_frontend.entity.Store;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddStoreDrink#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStoreDrink extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddStoreDrink() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddStoreDrink.
     */
    // TODO: Rename and change types and number of parameters
    public static AddStoreDrink newInstance(String param1, String param2) {
        AddStoreDrink fragment = new AddStoreDrink();
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
        View mainView = inflater.inflate(R.layout.fragment_add_store_drink, container, false);
        EditText textName = (EditText) mainView.findViewById(R.id.editTextAddDrinkName);
        String defaultName = textName.getText().toString();
        EditText textIngredientOne = (EditText) mainView.findViewById(R.id.editTextAddDrinkIngredientOne);
        EditText textIngredientTwo = (EditText) mainView.findViewById(R.id.editTextAddDrinkIngredientTwo);
        EditText textIngredientThree = (EditText) mainView.findViewById(R.id.editTextAddDrinkIngredientThree);
        String defaultIngredient = textIngredientOne.getText().toString();
        EditText textPrice = (EditText) mainView.findViewById(R.id.editTextAddDrinkPrice);
        EditText textDiscount = (EditText) mainView.findViewById(R.id.editTextAddDrinkDiscount);
        String defaultDiscount = textDiscount.getText().toString();
        Button confirmDrink = (Button) mainView.findViewById(R.id.button_confirm_drink);
        @NonNull Store store = Objects.requireNonNull(((AddStoreActivity) requireActivity()).theStore.mStoreModel.getValue());


        confirmDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(textName.getText().toString()) || defaultName.equals(textName.getText().toString())){
                    Toast.makeText(getContext(), "Please Enter your drinkName", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        Drink drink = new Drink();
                        drink.setDrinkName(textName.getText().toString());
                        addIngredient(drink, textIngredientOne.getText().toString(), defaultIngredient);
                        addIngredient(drink, textIngredientTwo.getText().toString(), defaultIngredient);
                        addIngredient(drink, textIngredientThree.getText().toString(), defaultIngredient);
                        double price = Double.parseDouble(textPrice.getText().toString());
                        drink.setPrice(price);

                        if (defaultDiscount.equals(textDiscount.getText().toString())){
                            drink.setDiscount(0.0);
                        }else{
                            double discount = Double.parseDouble(textDiscount.getText().toString());
                            if (discount > price || discount < 0){
                                throw new NumberFormatException();
                            }
                            drink.setDiscount(discount);
                        }
                        store.getMenu().add(drink);
                        Navigation.findNavController(view).navigate(R.id.action_drink_to_menu);
                    }catch (NumberFormatException e){
                        Toast.makeText(getContext(), "Please Enter a valid price/discount", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return mainView;
    }

    private void addIngredient(Drink drink, String ingredient, @NonNull String defaultIngredient){
        if (!defaultIngredient.equals(ingredient) && !"".equals(ingredient)){
            drink.getIngredients().add(ingredient);
        }
    }
}