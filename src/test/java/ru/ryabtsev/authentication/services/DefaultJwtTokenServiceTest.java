package ru.ryabtsev.authentication.services;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.ryabtsev.authentication.entities.User;

class DefaultJwtTokenServiceTest {

    private final UserService userService = new SimpleUserService();

    private final GameService gameService = new SimpleGameService();

    private final JwtTokenService jwtTokenService = new DefaultJwtTokenService(userService, gameService);


    @Test
    void tokenGenerationTest() {


    }

//    private static class TestUserService implements UserService {
//        @Override
//        public boolean validatesCredentials(String userName, String password) {
//            return false;
//        }
//
//        @Override
//        public Long getIdByUserName(String userName) {
//            return 1L;
//        }
//    }
}