package com.abc.localproduceapp;

import android.graphics.Bitmap;

public class MyData {
    private String ItemName;
    private String ItemPrice;
    private Bitmap ItemImage;
    private String ItemSeller;
    private  String token;

    public String getItemPrice() {
        return ItemPrice;
    }

    public String getToken() {  return token; }

    public void setToken(String token) {
        this.token = token;
    }

    public Bitmap getItemImage() {
        return ItemImage;
    }

    public String getItemSeller() {
        return ItemSeller;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public void setItemPrice(String itemPrice) {
        ItemPrice = itemPrice;
    }

    public void setItemImage(Bitmap itemImage) {
        ItemImage = itemImage;
    }

    public void setItemSeller(String itemSeller) {
        ItemSeller = itemSeller;
    }

    public MyData(String ItemName, String ItemPrice, String ItemSeller, Bitmap ItemImage, String token){
        this.ItemName =  ItemName;
        this.ItemPrice = ItemPrice;
        this.ItemSeller = ItemSeller;
        this.ItemImage = ItemImage;
        this.token =  token;
    }

}
