package ru.ryabtsev.starship.network.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * The class that represents message that has been sent by a client.
 */
@Getter
public class ActionMessage {


    private final String gameId;

    private final String objectId;

    private final String action;

    private final List<Object> parameters;

    public ActionMessage(
            @JsonProperty("game") final String gameId,
            @JsonProperty("object") final String objectId,
            @JsonProperty("action") final String action,
            @JsonProperty("parameters") final List<Object> parameters) {
        this.gameId = Objects.requireNonNull(gameId);
        this.objectId = Objects.requireNonNull(objectId);
        this.action = Objects.requireNonNull(action);
        this.parameters = Optional.ofNullable(parameters).orElse(Collections.emptyList());
    }
}
