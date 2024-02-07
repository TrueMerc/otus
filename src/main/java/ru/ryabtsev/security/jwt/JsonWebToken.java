package ru.ryabtsev.security.jwt;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import lombok.SneakyThrows;

public class JsonWebToken {

    private static final String SEPARATOR = ".";

    private static final String SEPARATOR_REGEX = "\\.";

    private static final String HMAC_SHA_ALGORITHM = "HmacSHA256";

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final String encodedHeader;

    @Getter
    private final String encodedPayload;

    private final String signature;

    private final String stringForm;

    public JsonWebToken(final String header, final String payload, final String secretKey) {
        encodedHeader = encodeToBase64(removeExcessSpaceCharacters(header));
        encodedPayload = encodeToBase64(removeExcessSpaceCharacters(payload));
        signature = hmacSha256(HMAC_SHA_ALGORITHM, String.join(SEPARATOR, encodedHeader, encodedPayload) , secretKey);
        stringForm = String.join(SEPARATOR, encodedHeader, encodedPayload, signature);
    }

    public JsonWebToken(final String tokenString) {
        final String[] tokenParts = tokenString.split(SEPARATOR_REGEX);
        encodedHeader = tokenParts[0];
        encodedPayload = tokenParts[1];
        signature = tokenParts[2];
        stringForm = tokenString;
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

    /**
     * Returns true if the token signature was created by using the same key or false in the other case.
     * @param secretKey secret key.
     * @return true if the token signature was created by using the same key or false in the other case.
     */
   public boolean isValidFor(final String secretKey) {
        final String newSignature = hmacSha256(
                HMAC_SHA_ALGORITHM, String.join(SEPARATOR, encodedHeader, encodedPayload) , secretKey);
        return signature.equals(newSignature);
    }
}
