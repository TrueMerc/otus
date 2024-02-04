package ru.ryabtsev.authentication.services;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;

public class DefaultJwtTokenService implements JwtTokenService {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final String SECRET_KEY = "29d17f180f9dfc62bc7fd7fed48a61b5c5931de47a7f0def962c5ce91063a83c";

    private static final String HMAC_SHA_ALGORITHM = "HmacSHA256";

    private static final String DOT = ".";


    // language=JSON
//    private static final String HEADER = """
//            {
//                "typ": "JWT",
//                "alg": "HS256"
//            }
//            """;
    private static final String HEADER = """
            {
                "typ": "JWT",
                "alg": "HS256"
            }
            """;

    // language=JSON
    private static final String PAYLOAD_TEMPLATE = """
            {
                "iss": "Authentication Service",
                "sub": "authentication",
                "aud": "users",
                "userId": "%s"
            }
            """;

    private final UserService userService;

    public DefaultJwtTokenService(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public String createTokenFor(final String userName) {
        final String header = encodeToBase64(removeExcessSpaceCharacters(HEADER));
        final String payload = encodeToBase64(removeExcessSpaceCharacters(formPayload(userName)));
        final String signature = hmacSha256(HMAC_SHA_ALGORITHM, String.join(DOT, header, payload) , SECRET_KEY);
        return String.join(DOT, header, payload, signature);
    }

    private static String removeExcessSpaceCharacters(final String string) {
        StringBuilder result = new StringBuilder(string.length());
        boolean inQuotes = false;
        boolean escapeMode = false;
        for (char character : string.toCharArray()) {
            if (escapeMode) {
                result.append(character);
                escapeMode = false;
            } else if (character == '"') {
                inQuotes = !inQuotes;
                result.append(character);
            } else if (character == '\\') {
                escapeMode = true;
                result.append(character);
            } else if (!inQuotes && (character == ' ' || character == '\n' || character == '\r')) {
                continue;
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    private static String encodeToBase64(final String string) {
        return encodeToBase64(string.getBytes(DEFAULT_CHARSET));
    }

    private static String encodeToBase64(final byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String formPayload(final String userName) {
        return String.format(PAYLOAD_TEMPLATE, userService.getIdByUserName(userName));
    }

    @SneakyThrows
    private static String hmacSha256(final String algorithm, final String data, final String key) {
        final var secretKeySpec = new SecretKeySpec(key.getBytes(DEFAULT_CHARSET), algorithm);
        final Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return encodeToBase64(mac.doFinal(data.getBytes(DEFAULT_CHARSET)));
    }
}
