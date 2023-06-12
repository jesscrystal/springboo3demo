package com.example.entity;

import lombok.Data;

@Data
public class Restbean<T> {
    private int status;
    private boolean success;
    private T message;

    private Restbean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static <T> Restbean<T> success(){
        return new Restbean<>(200,true,null);
    }

    public static <T> Restbean<T> success(T data){
        return new Restbean<>(200,true,data);
    }
    public static <T> Restbean<T> failure(int status){
        return new Restbean<>(status,false,null);
    }
    public static <T> Restbean<T> failure(int status,T data){
        return new Restbean<>(status,false,data);
    }
}
