package com.sjh.multiwatch.domain;

import java.security.SecureRandom;
import java.util.Base64;

public final class ApiKeyGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final int KEY_BYTE_LENGTH = 32; // 256bit

    private ApiKeyGenerator() {
    }

    public static String generate() {
        byte[] randomBytes = new byte[KEY_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return ENCODER.encodeToString(randomBytes);
    }
}