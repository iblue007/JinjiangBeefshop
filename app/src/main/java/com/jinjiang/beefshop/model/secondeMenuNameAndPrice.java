package com.jinjiang.beefshop.model;

/**
 * Created by xuqunxing on 2017/12/11.
 */

public class secondeMenuNameAndPrice {

    private String Name;
    private int price;
    private boolean addfood;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAddfood() {
        return addfood;
    }

    public void setAddfood(boolean addfood) {
        this.addfood = addfood;
    }
}
