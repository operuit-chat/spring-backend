package at.operuit.restapi.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Response<T> {
    
    public int code;
    public T message;

}
