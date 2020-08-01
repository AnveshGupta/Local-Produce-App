package com.abc.localproduceapp;

public class User {

    String username;
    String password;

    User(String name, String pass){
        this.username = name;
        this.password = pass;
    }
    String getUsername(){
        return this.username;
    }
    String getPassword(){
        return this.password;
    }

}
