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
import java.lang.Thread;

import app.src.main.java.GoogleDrive;

public class Server {

    static public boolean runnning; // Check if server is running (and yes I know thats not the perfect way to do that)

    public static boolean doNothing() {
        return true;
    }

    public static void main(String[] args) throws IOException {

        int port = 80;
        HttpServer server = HttpServer.create(new java.net.InetSocketAddress(port), 0);
        

        
        // Define a context for handling POST requests

        server.createContext("/post", new GetHandler(server));
        
        // Start the server
        server.setExecutor(null); // Use the default executor
        server.start();
        runnning = true;
        
        System.out.println("Waiting for user to scan QR code...");
        System.out.println("Server is listening on port " + port);

        while (runnning) {
            try  { Thread.sleep(100); } catch (Exception e) {}
        }

        // The server has benn stopped

        System.out.println("Uploading into Google Drive...");

        // TODO: Upload to Google Drive
        try {
            new GoogleDrive().run();
        } catch (Exception e) {
            System.out.print("Cought error:");
            System.out.println(e);            
        }
        

    }
    
    static class GetHandler implements HttpHandler {

        private HttpServer server;

        public GetHandler(HttpServer getserver) {
            this.server = getserver;

            runnning = true;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {

                String paramsRaw = exchange.getRequestURI().getRawQuery();  // Get GET request parameters
                

                HashMap<String, String> parameterHashMap = new HashMap<String, String>();   // Define Hashmap of params
                
                System.out.println("Recived GET request from user with params:");

                if (paramsRaw!=null) {

                    String[] params = paramsRaw.toString().split("&");

                    String urlParameter = "url";    // Choose the parameter correcponding with url of the file

                    int urls = 0;   // Define numbers of those parameters for checking if there is only one url

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

                        // Handle the correct case
                        System.out.print("Got the adderess from user: ");
                        System.out.println(parameterHashMap.get(urlParameter));

                        try {

                            // TODO: Get URL address of the file



                            //Download the file

                            String fullURL = parameterHashMap.get(urlParameter) + file;

                            System.out.print("Downloading file ");
                            System.out.print(fullURL);
                            System.out.println("...");

                            new DownloadFile().download(fullURL, fileName);

                            System.out.print("Downloading file ");
                            System.out.print(fullURL);
                            System.out.println("...Success");

                            String response = "<h1>Everything seems good!<h1/>For now from you that's all.";
                            exchange.sendResponseHeaders(200, response.length());
                            OutputStream os = exchange.getResponseBody();
                            os.write(response.getBytes());
                            os.close();



                            System.out.println("Stopping server");
                            runnning = false;
                            this.server.stop(0);

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