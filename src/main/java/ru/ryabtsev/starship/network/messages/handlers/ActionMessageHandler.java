package ru.ryabtsev.starship.network.messages.handlers;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import ru.ryabtsev.starship.actions.messaging.ActionMessageProcessing;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.network.messages.ActionMessage;

public class ActionMessageHandler implements HttpHandler {

    private static final String OK_MESSAGE = "OK";

    private static final String BAD_REQUEST_MESSAGE = "Bad Request";

    private static final int OK_CODE = 200;

    private static final int BAD_REQUEST_CODE = 400;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CommandQueue commandQueue;

    private final ApplicationContext applicationContext;

    public ActionMessageHandler(final CommandQueue commandQueue, final ApplicationContext applicationContext) {
        this.commandQueue = commandQueue;
        this.applicationContext = applicationContext;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final var requestBody = exchange.getRequestBody();
        boolean success = true;
        try {
            final var actionMessage = OBJECT_MAPPER.readValue(requestBody, ActionMessage.class);
            commandQueue.add(new ActionMessageProcessing(applicationContext, actionMessage));
        } catch (final StreamReadException | DatabindException e) {
            success = false;
        }

        final String response = success ? OK_MESSAGE : BAD_REQUEST_MESSAGE;
        final int responseCode = success ? OK_CODE : BAD_REQUEST_CODE;

        try {
            exchange.sendResponseHeaders(responseCode, response.length());
            final var responseBody = exchange.getResponseBody();
            responseBody.write(response.getBytes(StandardCharsets.UTF_8));
            responseBody.close();
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
