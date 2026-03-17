package edu.escuelaing.arep;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebFramework {

    private static final Map<String, WebMethod> getRoutes = new ConcurrentHashMap<>();

    public static void get(String path, WebMethod method) {
        getRoutes.put(path, method);
    }

    public static WebMethod getRoute(String path) {
        return getRoutes.get(path);
    }
}