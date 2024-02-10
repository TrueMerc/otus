package ru.ryabtsev.authentication.entities;

import java.util.List;
import java.util.UUID;

public record Game(UUID id, List<User> users) {

    public boolean contains(final User user) {
        return users.stream().anyMatch(participant -> participant.login().equals(user.login()));
    }
}
