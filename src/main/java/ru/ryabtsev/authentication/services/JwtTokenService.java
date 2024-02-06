package ru.ryabtsev.authentication.services;

import ru.ryabtsev.authentication.entities.User;

public interface JwtTokenService {
    String createTokenFor(final User user);
}
