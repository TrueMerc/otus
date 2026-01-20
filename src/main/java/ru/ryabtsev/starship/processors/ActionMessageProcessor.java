package ru.ryabtsev.starship.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.ryabtsev.starship.actions.messaging.ActionMessageProcessing;
import ru.ryabtsev.starship.context.ApplicationContext;
import ru.ryabtsev.starship.context.ContextSelection;
import ru.ryabtsev.starship.executors.CommandQueue;
import ru.ryabtsev.starship.network.messages.ActionMessage;

@RequiredArgsConstructor
public class ActionMessageProcessor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String CONTEXT_SUFFIX = "Context";

    private static final String CONTEXT_SELECTION = ContextSelection.class.getSimpleName();

    private final ApplicationContext applicationContext;

    private final CommandQueue commandQueue;

    public void process(final String userName, final String requestBody) {
        try {
            final var actionMessage = OBJECT_MAPPER.readValue(requestBody, ActionMessage.class);
            final ApplicationContext userContext = applicationContext.resolve(
                    CONTEXT_SELECTION, new Object[]{userName + CONTEXT_SUFFIX});
            commandQueue.add(new ActionMessageProcessing(userContext, actionMessage));
        } catch (final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
