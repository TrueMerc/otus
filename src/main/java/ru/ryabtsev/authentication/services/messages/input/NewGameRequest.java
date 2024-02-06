package ru.ryabtsev.authentication.services.messages.input;

import java.util.List;
import ru.ryabtsev.authentication.entities.User;

public record NewGameRequest(List<String> logins) {
}
