package com.ionmind.sls_backend.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record ApiError(String timestamp, int status, String error, String message, String path) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(OffsetDateTime.now(ZoneOffset.UTC).toString(), status, error, message, path);
    }
}
