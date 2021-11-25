package at.operuit.restapi.controllers;

import at.operuit.restapi.OperuitMain;
import at.operuit.restapi.database.Row;
import at.operuit.restapi.database.query.QueryResult;
import at.operuit.restapi.models.Response;
import at.operuit.restapi.models.User;
import at.operuit.restapi.util.data.Hashing;
import at.operuit.restapi.util.data.RateLimiter;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/user")
@ResponseBody
public class UserDataController {

    @CrossOrigin
    @PostMapping("/data")
    public Response<Object> getUserData(@RequestHeader("User-Data") String requestData, @RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user) {
        if (RateLimiter.compute(Hashing.hash(requestData + userDeviceTempId), 1).get())
            return new Response<>(50, "Rate limit exceeded");
        String username = user.username();
        String password = user.password();
        if (username == null || password == null
                || password.length() < 3
                || username.length() != 64 || password.length() > 4096)
            return new Response<>(100, "One or more arguments provided are missing or invalid");
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ? AND `password` = ?", username, password);
        QueryResult result = query.join();
        if (!result.hasNext())
            return new Response<>(101, "Auth error");
        Row<?> row = result.next();
        user = new User(null, (String) row.get("display_name").value(), null, (String) row.get("birthday").value(), (String) row.get("gender").value());
        return new Response<>(200, user);
    }

    @CrossOrigin
    @PatchMapping("/data")
    public Response<String> updateData(@RequestHeader("User-Data") String requestData, @RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user) {
        if (RateLimiter.compute(Hashing.hash(requestData + userDeviceTempId), 1).get())
            return new Response<>(50, "Rate limit exceeded");
        String username = user.username();
        String password = user.password();
        if (username == null || password == null
                || password.length() < 3
                || username.length() != 64 || password.length() > 4096)
            return new Response<>(100, "One or more arguments provided are missing or invalid");
        CompletableFuture<QueryResult> query = OperuitMain.getInstance().getDatabaseService().execute(() -> "SELECT * FROM `users` WHERE `username` = ? AND `password` = ?", username, password);
        QueryResult result = query.join();
        if (!result.hasNext())
            return new Response<>(101, "Auth error");
        Row<?> row = result.next();
        String displayName = user.displayName() == null ? (String) row.get("display_name").value() : user.displayName();
        String birthday = user.birthday() == null ? (String) row.get("birthday").value() : user.birthday();
        String gender = user.gender() == null ? (String) row.get("gender").value() : user.gender();
        OperuitMain.getInstance().getDatabaseService().execute(() -> "UPDATE `users` SET `display_name` = ?, `birthday` = ?, `gender` = ? WHERE `username` = ?", displayName, birthday, gender, username).join();
        return new Response<>(200, "Success");
    }

}
