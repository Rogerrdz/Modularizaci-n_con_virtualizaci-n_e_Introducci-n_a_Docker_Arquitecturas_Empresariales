package edu.escuelaing.arep;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private Map<String, String> parameters;

    public HttpRequest(Map<String, String> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    public String getValue(String key) {
        return parameters.get(key);
    }

    public String getValues(String key) {
        return parameters.get(key);
    }
}