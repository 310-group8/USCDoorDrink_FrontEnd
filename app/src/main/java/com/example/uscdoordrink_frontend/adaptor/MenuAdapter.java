package com.example.uscdoordrink_frontend.adaptor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uscdoordrink_frontend.ViewMenuActivity;
import com.example.uscdoordrink_frontend.entity.Drink;
import com.example.uscdoordrink_frontend.R;
import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    Context context;
    ArrayList<Drink> drinks;
    private ButtonInterface buttonInterface;

    public MenuAdapter(Context context, ArrayList<Drink> drinks) {
        this.context = context;
        this.drinks = drinks;
    }

    public void buttonSetOnclick(ButtonInterface buttonInterface){
        this.buttonInterface=buttonInterface;
    }

    public interface ButtonInterface{
        public void onclick( View view,int position);
    }


    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_drink_menu, parent, false);
        return new MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuViewHolder holder, int position) {

        Drink drink = drinks.get(holder.getAdapterPosition());
        holder.drinkName.setText(drink.getDrinkName());
        holder.price.setText(String.valueOf(drink.getPrice()));
        holder.id_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonInterface!=null) {
                    buttonInterface.onclick(v,holder.getAdapterPosition());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return drinks.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder{

        TextView drinkName;
        TextView price;
        Button id_button;
        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            drinkName = itemView.findViewById(R.id.itemName);
            price = itemView.findViewById(R.id.price);
            id_button = itemView.findViewById(R.id.select);
        }
    }
}
