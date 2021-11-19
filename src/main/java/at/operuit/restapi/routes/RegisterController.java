package at.operuit.restapi.routes;

import at.operuit.restapi.OperuitMain;
import at.operuit.restapi.database.QueryResult;
import at.operuit.restapi.models.Response;
import at.operuit.restapi.models.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@ResponseBody
public class RegisterController {

    @PostMapping("/register")
    public Response<String> register(String username, String displayName, String password) {
        if (username == null || displayName == null || password == null
                || username.length() < 3 || displayName.length() < 3 || password.length() < 3
                || username.length() != 64 || displayName.length() > 128 || password.length() > 4096)
            return new Response<>(100, "One or more arguments provided are null or invalid");
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ?", username);
        QueryResult result = query.join();
        if (result.getRows().size() > 0)
            return new Response<>(101, "Username already exists");
        OperuitMain.getInstance().getDatabaseService().execute(() -> "INSERT INTO `users` (`username`, `display_name`, `password`) VALUES (?, ?, ?)",
                username, displayName, password).thenAccept((queryResult -> System.out.println("User " + username + " registered")));
        return new Response<>(200, "Success");
    }

}
