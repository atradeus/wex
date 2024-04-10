package com.wex.app;

/**
 * @author Anthony Merlo
 * @since 10/04/2024
 */
public class ApiException extends RuntimeException {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
