package at.operuit.restapi.controllers;

import at.operuit.restapi.OperuitMain;
import at.operuit.restapi.database.query.QueryResult;
import at.operuit.restapi.models.Response;
import at.operuit.restapi.models.User;
import at.operuit.restapi.util.data.Hashing;
import at.operuit.restapi.util.data.RateLimiter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

@RestController
@ResponseBody
public class AuthController {

    @CrossOrigin
    @PostMapping("/register")
    public Response<String> register(@RequestHeader("User-Data") String requestData, @RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user, HttpServletResponse response) {
        if (RateLimiter.compute(Hashing.hash(requestData + userDeviceTempId), 1).get()) {
            response.setStatus(50);
            return new Response<>(50, "Rate limit exceeded");
        }
        String username = user.username();
        String displayName = user.displayName();
        String password = user.password();
        if (username == null || displayName == null || password == null
                || displayName.length() < 3 || password.length() < 3
                || username.length() != 64 || displayName.length() > 128 || password.length() > 4096) {
            response.setStatus(100);
            return new Response<>(100, "One or more arguments provided are missing or invalid");
        }
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ?", username);
        QueryResult result = query.join();
        response.setStatus(result.hasNext() ? 200 : 101);
        if (result.hasNext())
            return new Response<>(101, "Username already exists");
        OperuitMain.getInstance().getDatabaseService().execute(() -> "INSERT INTO `users` (`username`, `display_name`, `password`) VALUES (?, ?, ?)",
                username, displayName, password).thenAccept((queryResult -> System.out.println("User " + username + " registered")));
        return new Response<>(200, "Success");
    }

    @CrossOrigin
    @PostMapping("/login")
    public Response<String> login(@RequestHeader("User-Data") String requestData, @RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user, HttpServletResponse response) {
        if (RateLimiter.compute(Hashing.hash(requestData + userDeviceTempId), 1).get()) {
            response.setStatus(50);
            return new Response<>(50, "Rate limit exceeded");
        }
        String username = user.username();
        String password = user.password();
        if (username == null || password == null
                || password.length() < 3
                || username.length() != 64 || password.length() > 4096) {
            response.setStatus(100);
            return new Response<>(100, "One or more arguments provided are missing or invalid");
        }
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ? AND `password` = ?", username, password);
        QueryResult result = query.join();
        response.setStatus(result.hasNext() ? 200 : 102);
        if (!result.hasNext())
            return new Response<>(102, "Auth error");
        return new Response<>(200, "Success");
    }

    @CrossOrigin
    @GetMapping("/salt")
    public Response<String> retrieveSalt(@RequestHeader("User-Data") String requestData, @RequestHeader("User-TempDevId") String userDeviceTempId, @RequestParam String username, HttpServletResponse response) {
        if (RateLimiter.compute(Hashing.hash(requestData + userDeviceTempId), 1).get()) {
            response.setStatus(50);
            return new Response<>(50, "Rate limit exceeded");
        }
        if (username == null || username.length() != 64) {
            response.setStatus(100);
            return new Response<>(100, "One or more arguments provided are missing or invalid");
        }
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ?", username);
        QueryResult result = query.join();
        response.setStatus(result.hasNext() ? 200 : 101);
        if (!result.hasNext())
            return new Response<>(101, "User does not exist");
        return new Response<>(200, result.next().get("password").toString().split(":", 2)[1]);
    }

}
