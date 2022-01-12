package at.operuit.restapi.models;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Response {

    private int responseCode;

}
