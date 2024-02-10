package ru.ryabtsev.authentication.entities;

import java.util.UUID;

public record User(UUID id, String login, String password) {
}
