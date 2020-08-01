package com.abc.localproduceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
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

public class Register extends AppCompatActivity {
EditText inpemail,inppassword,inprepassword;
TextView logret;
ProgressBar progressBar;
Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        inpemail = findViewById(R.id.Email);
        inppassword = findViewById(R.id.Password);
        inprepassword = findViewById(R.id.RePassword);
        logret = findViewById(R.id.gotologin);
        progressBar = findViewById(R.id.progressBar);
        register = (Button) findViewById(R.id.signup);
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        logret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
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


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailtext = inpemail.getText().toString().trim();
                String passwordtext = inppassword.getText().toString().trim();
                String repass = inprepassword.getText().toString().trim();

                if (TextUtils.isEmpty(emailtext)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordtext)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(repass)) {
                    Toast.makeText(getApplicationContext(), "Enter password again!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!passwordtext.equals(repass)) {
                    Toast.makeText(getApplicationContext(),"Passwords are not matching",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordtext.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                User newuser = new User(emailtext,passwordtext);

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Call<JsonElement> call = rcc.signup(newuser);
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                        if (response.code() == 200) {
                            JsonElement post = response.body();
                            JsonObject object =  post.getAsJsonObject();
                            String out = object.get("tokens").getAsString();
                            myEdit.putString("token",out);
                            myEdit.apply();
                            Intent newintent = new Intent(Register.this,Info_Main.class);
//                            newintent.putExtra("token" , out);
                            startActivity(newintent);
                            Register.this.finish();

                        } else if (response.code() == 404) {
                            Toast.makeText(Register.this, "Failed to Signup",
                                    Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Toast.makeText(Register.this, "error: "+ t.getMessage(),
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
