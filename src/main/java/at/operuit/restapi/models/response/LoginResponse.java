package at.operuit.restapi.models.response;

import lombok.Getter;

@Getter
public class LoginResponse extends SuccessResponse {

    private final String token;

    public LoginResponse(String token) {
        super(200, true);
        this.token = token;
    }
}
