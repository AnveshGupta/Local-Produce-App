package com.abc.localproduceapp;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start3);
        startActivity(new Intent(StartActivity.this,Login.class));
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//        Retrofit retrofit =  new Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:3000")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//        RetrofitInterface rcc = retrofit.create(RetrofitInterface.class);
//
//        Call<Void> call = rcc.start();
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//
//                if (response.code() == 200) {
//                    startActivity(new Intent(StartActivity.this,MainActivity.class));
//                } else if (response.code() == 404) {
//                    startActivity(new Intent(StartActivity.this,Login.class));
//                }
//            }
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(StartActivity.this, "error: "+ t.getMessage(),
//                        Toast.LENGTH_LONG).show();
//            }
//        });

    }
}