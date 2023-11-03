/* 
 * Wroten by Dawid Parkitny aka Hapsik1234
 */

package app.src.main.java;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.*;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) throws IOException {

        int port = 80;
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(port), 0);
        
        // Define a context for handling POST requests
        server.createContext("/post", new GetHandler(server));
        
        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();
        
        System.out.println("Server is listening on port " + port);

        

    }
    
    static class GetHandler implements HttpHandler {

        private HttpServer server;

        public GetHandler(HttpServer getserver) {
            this.server = getserver;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {

                String paramsRaw = exchange.getRequestURI().getRawQuery();  // Get GET request parameters
                

                HashMap<String, String> parameterHashMap = new HashMap<String, String>();   // Define Hashmap of params
                
                System.out.println("Recived GET request with params:");

                if (paramsRaw!=null) {

                    String[] params = paramsRaw.toString().split("&");

                    String urlParameter = "url";    // Choose the parameter correcponding for url of the file

                    int urls = 0;   // Numbers of those parameters for checking if there is only one url

                    for (int i=0;i<params.length;i++) {

                        // Fill hashmap with parameters
                        parameterHashMap.put(params[i].split("=")[0],params[i].split("=")[1]);
                        System.out.print(params[i].split("=")[0]);
                        System.out.print(": ");
                        System.out.println(parameterHashMap.get(params[i].split("=")[0]));

                        if (params[i].split("=")[0].equals(urlParameter)) {
                            urls++;
                        }
                    }

                    if (urls==1) {

                        String file="/asset/secret.txt";
                        String fileName="secret.txt";

                        // Handling the correct case of urls
                        System.out.println(parameterHashMap.get(urlParameter));

                        try {

                            new DownloadFile().download(parameterHashMap.get(urlParameter) + file, fileName);

                            System.out.println("Successfully downloaded file!");

                            String response = "<h1>Everything seems good!<h1/>For now from you that's all.";
                            exchange.sendResponseHeaders(200, response.length());
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.getBytes());
                            os.close();

                            System.out.println("Stopping server.");

                            this.server.stop(5);

                        } catch (IOException e) {
                            
                            System.err.print("Exeption error: ");
                            System.err.println(e);

                        }


                    } else {
                        System.out.println("There are too much or less url parameters! Check syntax and try again.");
                    }
                    
                } else {
                    System.out.println("No params");
                }

                
                

                // Responsing to client
                String response = "<h1>Something went wrong!<h1/>Please check syntax and try again later.";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

            } else {

                System.out.print("Recived ");
                System.out.print(exchange.getRequestMethod());
                System.out.println(" request is that an error?");

                String response = "Only GET requests are allowed";
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            System.out.print('\n');
        }
    }

    static class DownloadFile {
        public void download(String url, String fileName) throws IOException {
            InputStream in = new URL(url).openStream();
            Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}