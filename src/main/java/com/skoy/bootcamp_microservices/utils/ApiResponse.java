package com.skoy.bootcamp_microservices.utils;

public class ApiResponse<T> {

    private String message;
    private T data;
    private int statusCode;
    private boolean ok;
    private Object dataExtra;


    public ApiResponse() {
    }

    public ApiResponse(String message, T data, int statusCode) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
    }

    public ApiResponse(String message, T data, int statusCode, boolean ok) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.ok = ok;
    }

    public ApiResponse(String message, T data, int statusCode, Object dataExtra) {
        this.message = message;
        this.data = data;
        this.statusCode = statusCode;
        this.dataExtra = dataExtra;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getDataExtra() {
        return dataExtra;
    }

    public void setDataExtra(Object dataExtra) {
        this.dataExtra = dataExtra;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

}
