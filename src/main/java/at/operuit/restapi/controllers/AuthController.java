package at.operuit.restapi.controllers;

import at.operuit.restapi.OperuitMain;
import at.operuit.restapi.database.query.QueryResult;
import at.operuit.restapi.models.Response;
import at.operuit.restapi.models.User;
import at.operuit.restapi.models.response.LoginResponse;
import at.operuit.restapi.models.response.RawMessageResponse;
import at.operuit.restapi.models.response.SuccessResponse;
import at.operuit.restapi.util.data.Hashing;
import at.operuit.restapi.util.data.RateLimiter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@RestController
@ResponseBody
public class AuthController {

    public static Map<String, String> tokens = new HashMap<>();

    @CrossOrigin
    @PostMapping("/register")
    public Response register(@RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user, HttpServletResponse response) {
        if (RateLimiter.compute(Hashing.hash(userDeviceTempId), 1).get()) {
            response.setStatus(50);
            return new RawMessageResponse(50, "Rate limit exceeded");
        }
        String username = user.username();
        String displayName = user.displayName();
        String password = user.password();
        if (username == null || displayName == null || password == null
                || displayName.length() < 3 || password.length() < 3
                || username.length() != 64 || displayName.length() > 128 || password.length() > 4096) {
            response.setStatus(100);
            return new RawMessageResponse(100, "One or more arguments provided are missing or invalid");
        }
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ?", username);
        QueryResult result = query.join();
        if (result.hasNext())
            return new SuccessResponse(101, false);
        OperuitMain.getInstance().getDatabaseService().execute(() -> "INSERT INTO `users` (`username`, `display_name`, `password`) VALUES (?, ?, ?)",
                username, displayName, password).thenAccept((queryResult -> System.out.println("User " + username + " registered")));
        return new SuccessResponse(200, true);
    }

    @CrossOrigin
    @PostMapping("/login")
    public Response login(@RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user, HttpServletResponse response) {
        if (RateLimiter.compute(Hashing.hash(userDeviceTempId), 1).get()) {
            response.setStatus(50);
            return new RawMessageResponse(50, "Rate limit exceeded");
        }
        String username = user.username();
        String password = user.password();
        if (username == null || password == null
                || password.length() < 3
                || username.length() != 64 || password.length() > 4096) {
            response.setStatus(100);
            return new RawMessageResponse(100, "One or more arguments provided are missing or invalid");
        }
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ? AND `password` = ?", username, password);
        QueryResult result = query.join();
        if (!result.hasNext())
            return new RawMessageResponse(102, "Auth error");
        String sessionToken = generateRandomAlphanumericToken(64);
        tokens.put(sessionToken, username);
        return new LoginResponse(sessionToken); // todo encrypt session token
    }

    @CrossOrigin
    @GetMapping("/salt")
    public Response retrieveSalt(@RequestHeader("User-TempDevId") String userDeviceTempId, @RequestParam String username, HttpServletResponse response) {
        if (RateLimiter.compute(Hashing.hash(userDeviceTempId), 1).get()) {
            response.setStatus(50);
            return new RawMessageResponse(50, "Rate limit exceeded");
        }
        if (username == null || username.length() != 64) {
            response.setStatus(100);
            return new RawMessageResponse(100, "One or more arguments provided are missing or invalid");
        }
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ?", username);
        QueryResult result = query.join();
        response.setStatus(result.hasNext() ? 200 : 101);
        if (!result.hasNext())
            return new RawMessageResponse(101, "User does not exist");
        return new RawMessageResponse(200, result.next().get("password").toString().split(":", 2)[1]);
    }

    public static final String ALPHANUMERIC_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generateRandomAlphanumericToken(int length) {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < length; i++)
            token.append(ALPHANUMERIC_CHARS.charAt(new Random().nextInt(ALPHANUMERIC_CHARS.length())));
        return token.toString();
    }

}
