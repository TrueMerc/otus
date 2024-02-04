package ru.ryabtsev.authentication.services;

public interface JwtTokenService {
    String createTokenFor(String userName);
}
