package com.abc.localproduceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@SuppressWarnings("UnusedAssignment")
public class Main_List extends AppCompatActivity {
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    final RetrofitInterface rcc = retrofit.create(RetrofitInterface.class);
    Button signout;
    Button update;
    Button additem;
    Button changeuser;
    TextView view_username;
    String username;
    RecyclerView rv;
    SharedPreferences sh;
    String tokenstring;
    SharedPreferences.Editor myEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main__list);


        signout = (Button) findViewById(R.id.main_list_signout);
        update = (Button) findViewById(R.id.main_list_update_user);
        additem = (Button) findViewById((R.id.main_list_add_item));
        changeuser = (Button) findViewById(R.id.main_list_changeUser);
        view_username = (TextView) findViewById(R.id.main_list_username);
        rv = findViewById(R.id.list_item_view);
        sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        myEdit =  sh.edit();
        tokenstring = sh.getString("token", "");
        username = sh.getString("user", "");
        view_username.setText(username);

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEdit.clear();
                myEdit.apply();
                startActivity(new Intent(Main_List.this,MainActivity.class));
                Main_List.this.finish();
            }
        });

        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main_List.this,Add_Item.class));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main_List.this,Info_Main.class));
//                Toast.makeText(Main_List.this,Integer.toString(data.size()),Toast.LENGTH_LONG).show();
//                Toast.makeText(Main_List.this,data.get(1).getItemSeller(),Toast.LENGTH_LONG).show();
            }
        });

        changeuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEdit.clear();
                myEdit.apply();
                startActivity(new Intent(Main_List.this,Login.class));
                Main_List.this.finish();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        populatelist();
    }

    void populatelist(){

        Call<JsonElement> call = rcc.listitems(tokenstring);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                ArrayList<JsonObject> itemlist = new ArrayList<>();
                ArrayList<MyData> data = new ArrayList<>();
                if(response.code() == 200) {
                    JsonElement items = response.body();
                    JsonObject object = items.getAsJsonObject();
                    JsonArray jsonArray = object.getAsJsonArray("item_list");
                    for (JsonElement pa : jsonArray) {
                        JsonObject obj = pa.getAsJsonObject();
                        itemlist.add(obj);
                    }
                    for(int i = 0 ; i < itemlist.size() ; i++) {

                        JsonObject json = itemlist.get(i);
                        String item_name = json.get("item_name").getAsString();
                        String price = json.get("item_price").getAsString();
                        String img = json.get("item_Image").getAsString();
                        String item_token = json.get("_id").getAsString();
                        String sellername = json.get("item_sellername").getAsString();

                        Bitmap bitmap = null;
                        try {
                            byte[] encodeByte = Base64.decode(img, Base64.DEFAULT);
                            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        } catch (Exception e) {
                            e.getMessage();
                        }

                        data.add(new MyData("Item Name : " + item_name, "Price : " + price, "Seller : " + "seller : " + sellername , bitmap, item_token));
                    }

                    final ItemAdapter adapter = new ItemAdapter(data);
                    rv.setHasFixedSize(false);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Main_List.this);
                    rv.setLayoutManager(layoutManager);
                    rv.setAdapter(adapter);
                }
                if(response.code() == 404){
                    Toast.makeText(Main_List.this, "No items found",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(Main_List.this, "err:" + t.getMessage() , Toast.LENGTH_LONG).show();
            }
        });
    }


}