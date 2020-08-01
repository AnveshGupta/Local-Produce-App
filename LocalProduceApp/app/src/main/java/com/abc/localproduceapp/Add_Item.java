package com.abc.localproduceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Add_Item extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    Retrofit retrofit =  new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    RetrofitInterface rcc = retrofit.create(RetrofitInterface.class);
    ImageView edt_itemimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        edt_itemimage= (ImageView) findViewById(R.id.add_item_image);
        EditText edt_itemname =  (EditText) findViewById(R.id.add_item_name);
        EditText edt_itemprice = (EditText) findViewById(R.id.add_item_price);
        Button additem =  (Button) findViewById(R.id.add_item_button);
        edt_itemimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pick();
            }
        });


        additem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemname =  edt_itemname.getText().toString().trim();
                String itemprice =  edt_itemprice.getText().toString().trim();
                Bitmap itemimg = ((BitmapDrawable)edt_itemimage.getDrawable()).getBitmap();

                // Converting Bitmap To String to Save into a database

                ByteArrayOutputStream baos = new  ByteArrayOutputStream();
                itemimg.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b=baos.toByteArray();
                String temp=Base64.encodeToString(b, Base64.DEFAULT);

               // Converting Bitmap To String to Save into a database

                // Sending to the Database
                String tokenstring = sh.getString("token", "");
                JsonParser json =  new JsonParser();
                String st = "{ \"name\" : \"" +   itemname   + "\"," +
                        "\"price\" : \"" +   itemprice    + "\","+
                        "\"image\" : \"" +  temp + " \""+
                        "}";
                JsonElement infouser = json.parse(st);
                Call<String> call = rcc.additem(infouser,tokenstring);

                // Sending to the Database



                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.code() == 200) {
                                Intent mainlist = new Intent(Add_Item.this,Main_List.class);
                                String token = response.body();
                                startActivity(mainlist);
                                Add_Item.this.finish();
                        } else if (response.code() == 404) {
                            Toast.makeText(Add_Item.this, "Failed to Signup",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(Add_Item.this, "error: "+ t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void pick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        getIntent.setType("image/*");
//
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickIntent.setType("image/*");
//
//        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//
//        startActivityForResult(chooserIntent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();

                    // method 1
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        edt_itemimage.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // method 2

                    //try {
                    //    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    //    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    //    imageStream.close(;
                    //   iv.setImageBitmap(yourSelectedImage);
                    //} catch (FileNotFoundException e) {
                    //    e.printStackTrace();
                    //}

                    // method 3
                    // iv.setImageURI(selectedImage);
                }
                break;
        }
    }


}