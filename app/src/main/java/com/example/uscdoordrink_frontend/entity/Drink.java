package com.example.uscdoordrink_frontend.entity;



import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/18 2:15
 */


public class Drink {
    private String storeUID;


    private String drinkName;


    private List<String> ingredients;


    private double price;

    public String getStoreUID() {
        return storeUID;
    }

    public void setStoreUID(String storeUID) {
        this.storeUID = storeUID;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}