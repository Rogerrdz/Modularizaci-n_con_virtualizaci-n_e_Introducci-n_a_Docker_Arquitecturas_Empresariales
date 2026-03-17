package edu.escuelaing.arep;

import java.io.PrintWriter;

public class HttpResponse {

    private PrintWriter out;
    private int status = 200;
    private String contentType = "text/html";

    public HttpResponse(PrintWriter out) {
        this.out = out;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void send(String body) {
        out.println("HTTP/1.1 " + status + " OK");
        out.println("Content-Type: " + contentType);
        out.println();
        out.println(body);
        out.flush();
    }
}