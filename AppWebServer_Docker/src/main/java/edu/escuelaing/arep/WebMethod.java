package edu.escuelaing.arep;

@FunctionalInterface
public interface WebMethod {
    String execute(HttpRequest req, HttpResponse res);
}