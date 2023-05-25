package pt.unl.fct.di.scmu.smartrest.util;

import java.io.Serializable;

public class Item implements Serializable {

    private String name;
    private int quantity;
    private float price;

    public Item(String name, int quantity, float price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemName() {
        return name;
    }

    public int getItemQuantity() {
        return quantity;
    }

    public void setItemQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void add(){
        this.quantity++;
    }

    public void remove(){
        if(quantity > 0)
            quantity--;
    }

    public float getItemPrice() {
        return price;
    }
}
