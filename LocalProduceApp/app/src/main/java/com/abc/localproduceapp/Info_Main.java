package com.abc.localproduceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Info_Main extends AppCompatActivity {

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    RetrofitInterface rcc = retrofit.create(RetrofitInterface.class);
    String firstname,secondname,address,phone_number,costomer_type;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_main);
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String tokenstring = sh.getString("token", "");
        EditText firstnameedit,secondnameedit,addressedit,phonenumberedit;
        RadioGroup rg;
        firstnameedit = (EditText) findViewById(R.id.fnameedit);
        secondnameedit = (EditText) findViewById(R.id.snameedit);
        addressedit = (EditText) findViewById(R.id.addressedit);
        phonenumberedit =  (EditText) findViewById(R.id.phoneedit);
        rg =(RadioGroup) findViewById(R.id.radioGroup);
        Button update = (Button) findViewById(R.id.info_update);



        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = firstnameedit.getText().toString().trim();
                secondname = secondnameedit.getText().toString().trim();
                address = addressedit.getText().toString().trim();
                phone_number = phonenumberedit.getText().toString().trim();
                int selectedID = rg.getCheckedRadioButtonId();
                String s =  firstname+" "+secondname;
                RadioButton rb = (RadioButton)findViewById(selectedID);
                String costomer_type = rb.getText().toString().trim();


                JsonParser json =  new JsonParser();
                String st = "{ \"fullname\" : \"" +    s    + "\"," +
                        "\"address\" : \"" +   address    + "\","+
                        "\"phonenumber\" : \"" +phone_number   + "\","+
                        "\"costomer_type\" : \"" +costomer_type  + "\""+
                        "}";
                JsonElement infouser = json.parse(st);
                Call<JsonElement> call = rcc.update(infouser,tokenstring);
                call.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                        if (response.code() == 200) {
                            JsonElement post = response.body();
                            JsonObject object =  post.getAsJsonObject();
//                            String out = object.get("tokens").getAsString();
                            Intent newintent = new Intent(Info_Main.this,MainActivity.class);
//                          newintent.putExtra("token",out);
                            startActivity(newintent);
                            Info_Main.this.finish();

                        } else if (response.code() == 404) {
                            Toast.makeText(Info_Main.this, "Failed to Login",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonElement> call, Throwable t) {
                        Toast.makeText(Info_Main.this, "error: "+ t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                });
            }
        });
    }
}
