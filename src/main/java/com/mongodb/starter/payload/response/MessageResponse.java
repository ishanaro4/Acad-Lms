package com.mongodb.starter.payload.response;

import org.springframework.beans.factory.annotation.Autowired;

public class MessageResponse {
    private String message;
    @Autowired
    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}