package ru.ryabtsev.starship.network;

import static org.junit.jupiter.api.Assertions.*;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultHttpServerTest {

    private static final String URL = "localhost";

    private static final int PORT = 9080;

    private static final String COMMAND_ENDPOINT = "/api/command";

    private final ApplicationHttpServer server = new DefaultHttpServer(
            new InetSocketAddress(URL, PORT), 100);

    @BeforeEach
    void setUp() {
        //server.registerHandler(COMMAND_ENDPOINT, new RequestCounter());
//        server.start();
    }

    @AfterEach
    void tearDown() {
        server.unregisterHandler(COMMAND_ENDPOINT);
        server.stop();
    }

    @Test
    @SneakyThrows
    void requestCountTest() {
        final RequestCounter requestCounter = new RequestCounter();
        server.registerHandler(COMMAND_ENDPOINT, requestCounter);
        server.start();
        final HttpClient client = HttpClient.newHttpClient();
         final int numberOfRequests = 10;
        IntStream.range(0, numberOfRequests).forEach((number) -> {
            try {
                final HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI("http://" + URL + ":" + PORT + COMMAND_ENDPOINT))
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException | URISyntaxException e) {
                throw new IllegalStateException(e);
            }
        });
        assertEquals(numberOfRequests, requestCounter.getNumberOfRequests());
    }


    private static class RequestCounter implements HttpHandler {

        private int numberOfRequests = 0;

        @Override
        public void handle(final HttpExchange exchange) {
            numberOfRequests++;
            try {
                exchange.sendResponseHeaders(200, 0);
                final var responseBody = exchange.getResponseBody();
                responseBody.write("".getBytes());
                responseBody.close();
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public int getNumberOfRequests() {
            return numberOfRequests;
        }
    }
}