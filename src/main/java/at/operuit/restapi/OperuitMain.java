package at.operuit.restapi;

import at.operuit.restapi.database.Credentials;
import at.operuit.restapi.database.DatabaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;

@Getter
public class OperuitMain {

    @Getter
    private static OperuitMain instance;
    private DatabaseService databaseService;
    
    public void run() throws IOException {
        instance = this;
        // Get credentials and initialize database service
        Credentials credentials = new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream("credentials.json"), Credentials.class);
        databaseService = new DatabaseService(credentials);
        databaseService.execute(() -> "CREATE TABLE IF NOT EXISTS `users` (`username` VARCHAR(64) NOT NULL PRIMARY KEY, `display_name` VARCHAR(128) NOT NULL, `password` VARCHAR(4096) NOT NULL, `birthday` VARCHAR(24), `gender` VARCHAR(24), `private_key` TEXT)").thenAccept(queryResult -> {});
    }
    
}