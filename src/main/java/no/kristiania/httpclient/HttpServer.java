package no.kristiania.httpclient;


import no.kristiania.database.WorkerDao;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class HttpServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private WorkerDao workerDao;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        workerDao = new WorkerDao(dataSource);
        // Open an entry point to our program for network clients
        ServerSocket serverSocket = new ServerSocket(port);

        // New threads executes the code in a separate "thread", that is: In parallel
        new Thread(() -> {  // Anonymous function with code that will be executed in parallel (INFINITE LOOP!!)
            while (true){
                try {
                    // Accept waits for a client to try and connect - blocks until a connection is successful
                    Socket clientSocket = serverSocket.accept();
                    handleRequest(clientSocket);
                }catch (IOException | SQLException e) {
                    // If something went wrong with the connection - print out exception and try again
                    e.printStackTrace();
                }
            }
        }).start();  // Start the threads, so the code inside executes without blocking the current thread
        // Now the test does NOT have to wait for someone to connect
    }
    // This code will be executed for each client (connection)
    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();
        System.out.println("REQUEST " + requestLine);
        // Example "GET /echo?body=hello HTTP/1.1"  (this is what the browser writes)

        // Example GET, POST, PUT, DELETE etc
        String requestMethod = requestLine.split(" ")[0];

        String requestTarget = requestLine.split(" ")[1];
        // Example "GET /echo?body=hello"

        int questionPos = requestTarget.indexOf('?');

        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if(requestMethod.equals("POST")){
            QueryString requestedParameter = new QueryString(request.getBody());

            workerDao.insert(requestedParameter.getParameter("first_name"));
            String body = "Okay";
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "\r\n" +
                    body;
            // Write the response back to the client
            clientSocket.getOutputStream().write(response.getBytes());
        } else {
            if (requestPath.equals("/echo")) {
                handleEchoRequest(clientSocket, requestTarget, questionPos);
            } else if (requestPath.equals("/api/showWorker")){
                handleGetWorkers(clientSocket);
            } else {
                handleFileRequest(clientSocket, requestPath);
                //return;
            }
        }
    }

    private void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if(inputStream == null){
                String body = requestPath + " does not exist";
                String response = "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "\r\n" +
                        body;
                clientSocket.getOutputStream().write(response.getBytes());
                return;
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);

            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            }
            if (requestPath.endsWith(".css")){
                contentType = "text/css";
            }

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            clientSocket.getOutputStream().write(response.getBytes());
            clientSocket.getOutputStream().write(buffer.toByteArray());
        }
    }

    private void handleGetWorkers(Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (String workerName : workerDao.list()) {
            body += "<li>" + workerName + "</li>";
        }

        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    private void handleEchoRequest(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if (questionPos != -1) {
            // "body=hello"
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }
        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Contention: close\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }

    public static void main(String[] args) throws IOException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/kristianiaworker");
        dataSource.setUser("kristianiashop");
        dataSource.setPassword("harasilaw");

        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:{}/showWorker.html", 8080);
    }


    public List<String> getWorkerNames() throws SQLException {
        return workerDao.list();
    }
}
