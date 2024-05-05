package com.example.hushtalk;

public class users {

    String userId,username,emaill, password;

    public users() {
    }



    public users(String userId,String username, String emaill, String password) {
        this.userId = userId;
        this.username = username;
        this.emaill = emaill;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getEmaill() {
        return emaill;
    }

    public void setEmaill(String emaill) {
        this.emaill = emaill;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}