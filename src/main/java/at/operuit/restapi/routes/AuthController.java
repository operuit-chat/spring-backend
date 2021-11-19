package at.operuit.restapi.routes;

import at.operuit.restapi.OperuitMain;
import at.operuit.restapi.database.query.QueryResult;
import at.operuit.restapi.models.Response;
import at.operuit.restapi.util.data.Hashing;
import at.operuit.restapi.util.data.RateLimiter;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@ResponseBody
public class AuthController {

    @GetMapping("/register")
    public Response<String> register(@RequestHeader("User-Data") String requestData, String username, String displayName, String password) {
        if (!RateLimiter.compute(Hashing.hash(requestData)).acquire())
            return new Response<>(50, "Rate limit exceeded");
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
