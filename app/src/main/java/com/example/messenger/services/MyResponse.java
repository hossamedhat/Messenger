package com.example.messenger.services;

public class MyResponse {
    public int success;

    public MyResponse() {
    }

    public MyResponse(int success) {
        this.success = success;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
