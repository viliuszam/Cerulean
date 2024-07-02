package me.vilius.cerulean.controller.dto;

import me.vilius.cerulean.model.User;

public class SignupRequest {
    private String username;
    private String realName;
    private String email;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toUser(){
        User requestUser = new User();
        requestUser.setUsername(this.getUsername());
        requestUser.setPassword(this.getPassword());
        requestUser.setRealName(this.getRealName());
        requestUser.setEmail(this.getEmail());
        return requestUser;
    }

}
