package at.operuit.restapi.models.response;

import at.operuit.restapi.models.Response;
import lombok.Getter;

@Getter
public class SuccessResponse extends Response {

    private final boolean success;

    public SuccessResponse(int responseCode, boolean success) {
        super(responseCode);
        this.success = success;
    }
}
