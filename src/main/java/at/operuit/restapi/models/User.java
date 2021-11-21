package at.operuit.restapi.models;

public record User(String username, String displayName, String password, String birthday, String gender) {
}
