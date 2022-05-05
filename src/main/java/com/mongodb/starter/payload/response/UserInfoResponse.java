package com.mongodb.starter.payload.response;

import java.util.List;

public class UserInfoResponse {
    private String id;
    private String username;
    private int mobile;

    public UserInfoResponse(String id, String username ,int mobile) {
        this.id = id;
        this.username = username;
        this.mobile = mobile;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}