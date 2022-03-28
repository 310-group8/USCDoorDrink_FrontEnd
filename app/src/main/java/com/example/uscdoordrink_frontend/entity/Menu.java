package com.example.uscdoordrink_frontend.entity;

import java.util.List;


public class Menu {
    public List<Drink> drinks;

    public Menu(List<Drink> drinks) {
        this.drinks = drinks;
    }

    public Menu(){}

    public List<Drink> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Drink> drinks) {
        this.drinks = drinks;
    }
}
