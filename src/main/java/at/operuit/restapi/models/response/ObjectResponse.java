package at.operuit.restapi.models.response;

import at.operuit.restapi.models.Response;
import lombok.Getter;

@Getter
public class ObjectResponse extends Response {

    private final Object message;

    public ObjectResponse(int responseCode, Object message) {
        super(responseCode);
        this.message = message;
    }
}
