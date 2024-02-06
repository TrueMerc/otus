package ru.ryabtsev.authentication.jwt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;

public class JsonWebToken {

    private static final String SEPARATOR = ".";

    private static final String HMAC_SHA_ALGORITHM = "HmacSHA256";

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final String encodedHeader;

    private final String encodedPayload;

    private final String signature;

    private final String stringForm;

    public JsonWebToken(final String header, final String payload, final String secretKey) {
        encodedHeader = encodeToBase64(removeExcessSpaceCharacters(header));
        encodedPayload = encodeToBase64(removeExcessSpaceCharacters(payload));
        signature = hmacSha256(HMAC_SHA_ALGORITHM, String.join(SEPARATOR, encodedHeader, encodedPayload) , secretKey);
        stringForm = String.join(SEPARATOR, encodedHeader, encodedPayload, signature);
    }

    private static String encodeToBase64(final String string) {
        return encodeToBase64(string.getBytes(DEFAULT_CHARSET));
    }

    private static String encodeToBase64(final byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
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

    @SneakyThrows
    private static String hmacSha256(final String algorithm, final String data, final String key) {
        final var secretKeySpec = new SecretKeySpec(key.getBytes(DEFAULT_CHARSET), algorithm);
        final Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return encodeToBase64(mac.doFinal(data.getBytes(DEFAULT_CHARSET)));
    }

    @Override
    public String toString() {
        return stringForm;
    }
}
