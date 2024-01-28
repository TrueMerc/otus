package ru.ryabtsev.starship.network.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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
        this.gameId = gameId;
        this.objectId = objectId;
        this.action = action;
        this.parameters = parameters;
    }
}
