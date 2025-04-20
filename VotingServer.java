import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class VotingServer {
    private static final Map<String, Integer> votes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // Create HTTP server
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Serve static HTML file (index.html)
        server.createContext("/", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                File file = new File("index.html");
                if (file.exists()) {
                    byte[] response = Files.readAllBytes(file.toPath());
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                } else {
                    String response = "<h1>File not found</h1>";
                    exchange.sendResponseHeaders(404, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }));

        // Serve static CSS file
        server.createContext("/styles.css", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                File file = new File("styles.css");
                if (file.exists()) {
                    byte[] response = Files.readAllBytes(file.toPath());
                    exchange.getResponseHeaders().set("Content-Type", "text/css");
                    exchange.sendResponseHeaders(200, response.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response);
                    os.close();
                } else {
                    String response = "<h1>CSS File not found</h1>";
                    exchange.sendResponseHeaders(404, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        }));

        // Handle voting submission
        server.createContext("/vote", (exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                
                // Extract the candidate from form data
                String candidate = sb.toString().split("=")[1];
                votes.put(candidate, votes.getOrDefault(candidate, 0) + 1);

                // Create the response with voting results
                StringBuilder response = new StringBuilder("<h1>Thank you for voting!</h1><p>Current Results:</p>");
                for (Map.Entry<String, Integer> entry : votes.entrySet()) {
                    response.append("<p>").append(entry.getKey()).append(": ").append(entry.getValue()).append(" votes</p>");
                }
                response.append("<a href='/'>Back to Vote</a>");

                byte[] responseBytes = response.toString().getBytes();
                exchange.sendResponseHeaders(200, responseBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(responseBytes);
                os.close();
            }
        }));

        // Start the server
        server.setExecutor(null); // Default executor
        server.start();
        System.out.println("Server is running on http://localhost:8080");
    }
}
