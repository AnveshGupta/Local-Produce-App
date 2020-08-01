package com.abc.localproduceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Console;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    EditText email,password;
    Button login;
    TextView register;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
            }
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit =  new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        final RetrofitInterface rcc = retrofit.create(RetrofitInterface.class);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext = email.getText().toString().trim();
                String passwordtext = password.getText().toString().trim();
                User newuser = new User(emailtext,passwordtext);
                if (TextUtils.isEmpty(emailtext)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(passwordtext)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                Call<JsonElement> call = rcc.login(newuser);

                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                        if (response.code() == 200) {
                            JsonElement post = response.body();
                            JsonObject object =  post.getAsJsonObject();
                            String out = object.get("tokens").getAsString();
                            myEdit.putString("token", out);
                            myEdit.apply();
                            Intent newintent = new Intent(Login.this,MainActivity.class);
//                          newintent.putExtra("token",out);
                            startActivity(newintent);
                            Login.this.finish();
                        } else if (response.code() == 404) {
                            Toast.makeText(Login.this, "Wrong Credentials",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Toast.makeText(Login.this, "error: "+ t.getMessage(),
                                Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                });


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
