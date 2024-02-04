package ru.ryabtsev.starship.network.messages;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class ActionMessageTest {

    @Test
    void clientMessageFileExistenceTest() {
        assertTrue(new File("./src/test/resources/ClientMessage.json").exists());
    }

    @Test
    @SneakyThrows
    void clientMessageDeserializationTest() {
        final File file = new File("./src/test/resources/ClientMessage.json");
        final ObjectMapper objectMapper = new ObjectMapper();
        final ActionMessage message = objectMapper.readValue(file, ActionMessage.class);

        assertEquals("9c5b94b1-35ad-49bb-b118-8e8fc24abf80", message.getGameId());
        assertEquals("9c5b94b1-35ad-49bb-b118-8e8fc24abf81", message.getObjectId());
        assertEquals("movement", message.getAction());
        final List<Object> parameters = message.getParameters();
        assertEquals(3, parameters.size());
        assertTrue(parameters.get(0) instanceof String);
        assertEquals("string", parameters.get(0));
        assertTrue(parameters.get(1) instanceof Integer);
        assertEquals(42, ((Integer) parameters.get(1)).intValue());
        assertTrue(parameters.get(2) instanceof Double);
        assertTrue(Math.abs(3.14 - ((Double) parameters.get(2)).doubleValue()) < 1e-12);
    }
}