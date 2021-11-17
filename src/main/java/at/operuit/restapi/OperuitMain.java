package at.operuit.restapi;

import at.operuit.restapi.database.Credentials;
import at.operuit.restapi.database.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class OperuitMain {

    public void run() throws IOException {
        // Get credentials and initialize database service
        Credentials credentials = new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream("credentials.json"), Credentials.class);
        DatabaseService databaseService = new DatabaseService(credentials);
        
        databaseService.execute(() -> "CREATE TABLE IF NOT EXISTS `users` (username VARCHAR(64), displayName VARCHAR(128), password TEXT, birthday VARCHAR(24), gender VARCHAR(24), privateKey TEXT)").thenAccept(mariadbResult -> {});
    }
    
}