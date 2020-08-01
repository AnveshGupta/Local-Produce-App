package com.abc.localproduceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.savedstate.SavedStateRegistryOwner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.appmain);
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sh.edit();
//      String tokenstring = getIntent().getStringExtra("token");
        String tokenstring = sh.getString("token", "");
        String json =  "{ \"tokens\" : \""+ tokenstring + "\" }";
        JsonParser parser = new JsonParser();
        JsonElement jse = parser.parse(json);
        Call<JsonElement> call = rcc.mainactivity(jse,tokenstring);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.code() == 200) {
                    JsonElement user = response.body();
                    JsonObject object =  user.getAsJsonObject();
                    String out = object.get("name").getAsString();
                    myEdit.putString("user", out);
                    myEdit.apply();
                    startActivity(new Intent(MainActivity.this,Main_List.class));
                    MainActivity.this.finish();
                } else if (response.code() == 404) {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    MainActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(MainActivity.this, "error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this,StartActivity.class));
//                Toast.makeText(MainActivity.this, tokenstring, Toast.LENGTH_SHORT).show();
//                  startActivity(new Intent(MainActivity.this,Info_Main.class));
//
//
//
//            }
//        });
    }
}

