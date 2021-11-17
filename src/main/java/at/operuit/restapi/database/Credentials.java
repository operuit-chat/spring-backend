package at.operuit.restapi.database;

public record Credentials(String hostname, int port, String username, String password, String database) {
}
