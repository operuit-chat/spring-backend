package at.operuit.restapi.models;

public record Response<T>(int code, T message) {
}
