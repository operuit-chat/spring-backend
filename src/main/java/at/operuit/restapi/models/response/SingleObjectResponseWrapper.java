package at.operuit.restapi.models.response;

import at.operuit.restapi.models.Response;

public abstract class SingleObjectResponseWrapper extends Response {

    public SingleObjectResponseWrapper(int responseCode) {
        super(responseCode);
    }

    public abstract Object getObject();

}
