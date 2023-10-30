package src;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import src.GoogleDrive;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Server {
    public static void main(String[] args) throws IOException {

        int port = 80; // You can change the port number if needed
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(port), 0);
        
        // Define a context for handling POST requests
        server.createContext("/post", new PostHandler());
        
        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();
        
        System.out.println("Server is listening on port " + port);
    }
    
    static class PostHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                InputStream input = exchange.getRequestBody();
                InputStreamReader isr = new InputStreamReader(input);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder request = new StringBuilder();
                String line;

                String queryString = exchange.getRequestURI().getRawQuery();
/* 
                while ((line = br.readLine()) != null) {
                    request.append(line);
                }
                br.close();
*/                
                // Print the POST request data
                System.out.println("Received GET request:");
                System.out.println(request.toString());
                System.out.println(queryString);
                
                // You can add code here to process the POST request data as needed
                
                String response = "GET request received successfully";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                // Handle other HTTP methods (e.g., GET) here
                String response = "Only GET requests are allowed";
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}