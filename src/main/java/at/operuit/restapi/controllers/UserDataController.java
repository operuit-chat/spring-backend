package at.operuit.restapi.controllers;

import at.operuit.restapi.OperuitMain;
import at.operuit.restapi.database.Row;
import at.operuit.restapi.database.query.QueryResult;
import at.operuit.restapi.models.Response;
import at.operuit.restapi.models.User;
import at.operuit.restapi.models.response.ObjectResponse;
import at.operuit.restapi.models.response.RawMessageResponse;
import at.operuit.restapi.util.data.Hashing;
import at.operuit.restapi.util.data.RateLimiter;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/user")
@ResponseBody
public class UserDataController {

    @CrossOrigin
    @PostMapping("/data")
    public Response getUserData(@RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user, HttpServletResponse response) {
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
            return new RawMessageResponse(101, "Auth error");
        Row<?> row = result.next();
        user = new User(null, (String) row.get("display_name").value(), null, (String) row.get("birthday").value(), (String) row.get("gender").value());
        return new ObjectResponse(200, user);
    }

    @CrossOrigin
    @PatchMapping("/data")
    public Response updateData(@RequestHeader("User-TempDevId") String userDeviceTempId, @RequestBody User user, HttpServletResponse response) {
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
            return new RawMessageResponse(101, "Auth error");
        Row<?> row = result.next();
        String displayName = user.displayName() == null ? (String) row.get("display_name").value() : user.displayName();
        String birthday = user.birthday() == null ? (String) row.get("birthday").value() : user.birthday();
        String gender = user.gender() == null ? (String) row.get("gender").value() : user.gender();
        OperuitMain.getInstance().getDatabaseService().execute(() -> "UPDATE `users` SET `display_name` = ?, `birthday` = ?, `gender` = ? WHERE `username` = ?", displayName, birthday, gender, username).join();
        return new RawMessageResponse(200, "Success");
    }

}
