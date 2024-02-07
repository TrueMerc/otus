package ru.ryabtsev.starship.network;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.security.jwt.JsonWebToken;
import ru.ryabtsev.starship.actions.Command;
import ru.ryabtsev.starship.actions.movement.Movement;
import ru.ryabtsev.starship.actions.movement.Vector;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.context.SimpleApplicationContext;
import ru.ryabtsev.starship.exceptions.handlers.CommandExceptionHandler;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.executors.ConcurrentCommandQueue;
import ru.ryabtsev.starship.network.messages.ActionMessage;
import ru.ryabtsev.starship.network.messages.handlers.ActionMessageHandler;
import ru.ryabtsev.starship.objects.Starship;

class DefaultHttpServerTest {

    private static final String URL = "localhost";

    private static final int PORT = 9080;

    private static final String STARSHIP_ID = "9c5b94b1-35ad-49bb-b118-8e8fc24abf81";

    private static final String COMMAND_ENDPOINT = "/api/command";

    private static final String COMMAND_FILE_PATH = "./src/test/resources/MovementMessage.json";

    private static final double DELTA = 1.0e-12;

    private static final String SECRET_KEY = "29d17f180f9dfc62bc7fd7fed48a61b5c5931de47a7f0def962c5ce91063a83c";

    // language=JSON
    private static final String HEADER = """
            {
                "typ": "JWT",
                "alg": "HS256"
            }
            """;

    // language=JSON
    private static final String PAYLOAD_TEMPLATE = """
            {
                "iss": "Authentication Service",
                "sub": "authentication",
                "aud": "users",
                "userId": "%s",
                "gameId": "%s"
            }
            """;

    private static final URI REQUEST_URI;

    static {
        try {
            REQUEST_URI = new URI("http://" + URL + ":" + PORT + COMMAND_ENDPOINT);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
    private final ApplicationHttpServer server = new DefaultHttpServer(
            new InetSocketAddress(URL, PORT), 100);

    @AfterEach
    void tearDown() {
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
                        .uri(REQUEST_URI)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new IllegalStateException(e);
            }
        });
        assertEquals(numberOfRequests, requestCounter.getNumberOfRequests());
    }

    @Test
    @SneakyThrows
    void actionMessageHandlerTest() {
        // Arrange:
        final var registration = "DependencyRegistration";
        final ApplicationContext context = new SimpleApplicationContext();
        final CommandQueue commandQueue = new ConcurrentCommandQueue(new CommandExceptionHandler());
        final Function<Object[], Object> commandQueueProvider = (objects) -> commandQueue;
        context.<Command>resolve(registration, new Object[] { "MessageQueue", commandQueueProvider}).execute();
        ;

        final Starship starship = new Starship(new Vector(0., 0.), new Vector(1., 1.));
        final Function<Object[], Object> starshipProvider = (objects) -> starship;
        context.<Command>resolve(registration, new Object[] { STARSHIP_ID, starshipProvider}).execute();

        final Map<String, String> apiMap = Map.of("movement", Movement.class.getName());
        final Function<Object[], Object> apiMapProvider = objects -> apiMap;
        context.<Command>resolve("DependencyRegistration", new Object[] { "ApiMap", apiMapProvider }).execute();

        final ActionMessageHandler actionMessageHandler = new ActionMessageHandler(commandQueue, context);
        server.registerHandler(COMMAND_ENDPOINT, actionMessageHandler);
        server.start();

        final ObjectMapper objectMapper = new ObjectMapper();
        final ActionMessage actionMessage = objectMapper.readValue(new File(COMMAND_FILE_PATH), ActionMessage.class);
        final String payload = String.format(PAYLOAD_TEMPLATE, "1", actionMessage.getGameId());
        final JsonWebToken token = new JsonWebToken(HEADER, payload, SECRET_KEY);
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(REQUEST_URI)
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofFile(Path.of(COMMAND_FILE_PATH)))
                .build();

        // Act:
        final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert:
        assertEquals(200, response.statusCode());
        assertEquals("OK", response.body());
        assertFalse(commandQueue.isEmpty());
        commandQueue.execute();
        assertFalse(commandQueue.isEmpty());
        commandQueue.execute();
        assertEquals(1., starship.getPosition().x(), DELTA);
        assertEquals(1., starship.getPosition().y(), DELTA);
    }


    private static class RequestCounter implements HttpHandler {

        private int numberOfRequests;

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

        int getNumberOfRequests() {
            return numberOfRequests;
        }
    }
}