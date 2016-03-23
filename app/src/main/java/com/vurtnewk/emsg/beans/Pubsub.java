package com.vurtnewk.emsg.beans;

import java.util.ArrayList;

public class Pubsub {

    private ArrayList<Item> items = null;

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Pubsub [items=" + items + "]";
    }

}
