package no.kristiania.httpclient;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private final int statusCode;
    private Map<String, String> responseHeaders = new HashMap<>();
    private String responseBody;

    public HttpClient(final String hostname, int port, final String requestTarget) throws IOException {
        this(hostname, port, requestTarget, "GET", null);
    }

    public HttpClient(final String hostname, int port, String requestTarget, final String httpMethod, String requestBody) throws IOException {
        Socket socket = new Socket(hostname, port);

        String contentLengthHeader = requestBody != null ? "Content-Length: " + requestBody.length() + "\r\n" : "";
        // Format as specified in the HTTP specification
        // Each responseLine is separated by \r\n (CRLF)
        // The request ends with an empty responseLine (\r\n\r\n)
        String request = httpMethod + " " + requestTarget + " HTTP/1.1\r\n" +
                "Host: " + hostname + "\r\n" +
                contentLengthHeader +
                "\r\n";
        // Writes data to the server
        socket.getOutputStream().write(request.getBytes());

        if (requestBody != null){
            socket.getOutputStream().write(requestBody.getBytes());
        }


        HttpMessage response = new HttpMessage(socket);
        String responseLine = response.getStartLine();
        responseHeaders = response.getHeaders();
        responseBody = response.getBody();

        String[] responseLineParts = responseLine.split(" ");

        statusCode = Integer.parseInt(responseLineParts[1]);
    }

    public static void main(String[] args) throws IOException {
        String hostname = "urlecho.appspot.com";
        int port = 80;
        String requestTarget = "/echo?status=200&body=Hello%20world!";
        new HttpClient(hostname, port, requestTarget);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseHeader(String headerName) {
        return responseHeaders.get(headerName);
    }

    public String getResponseBody() {
        return responseBody;
    }
}