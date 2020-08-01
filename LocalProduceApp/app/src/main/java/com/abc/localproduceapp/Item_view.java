package com.abc.localproduceapp;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Item_view extends AppCompatActivity {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    RetrofitInterface rcc = retrofit.create(RetrofitInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_view);
        TextView itemname = (TextView) findViewById(R.id.item_view_itemname);
        TextView itemprice = (TextView) findViewById(R.id.item_view_price);
        TextView seller = (TextView) findViewById(R.id.item_view_seller);
        TextView selleraddress = (TextView) findViewById(R.id.item_view_selleraddress);
        ImageView itemImage = (ImageView) findViewById(R.id.item_view_itemimage);
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String tokenstring = sh.getString("token", "");
        String item_token = getIntent().getStringExtra("token");
        Call<JsonElement> call = rcc.getitem(item_token,tokenstring);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonElement post = response.body();
                JsonObject object =  post.getAsJsonObject();
                String item_name = object.get("item_name").getAsString();
                String item_price = "Price : "  + object.get("item_price").getAsString();
                String item_image = object.get("item_Image").getAsString();
                String seller_name = "Seller : " + object.get("seller_name").getAsString();
                String seller_address ="Address : " + object.get("seller_address").getAsString();
                itemname.setText(item_name);
                itemprice.setText(item_price);
                seller.setText(seller_name);
                selleraddress.setText(seller_address);
                Bitmap bitmap = null;
                try {
                    byte [] encodeByte= Base64.decode(item_image, Base64.DEFAULT);
                    bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                } catch(Exception e) {
                    e.getMessage();
                }
                itemImage.setImageBitmap(bitmap);

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(Item_view.this,"error",Toast.LENGTH_SHORT).show();
            }
        });
    }
}