package at.operuit.restapi.models.response;

import at.operuit.restapi.models.Response;
import lombok.Getter;

@Getter
public class RawMessageResponse extends Response {

    private final String message;

    public RawMessageResponse(int responseCode, String message) {
        super(responseCode);
        this.message = message;
    }
}
