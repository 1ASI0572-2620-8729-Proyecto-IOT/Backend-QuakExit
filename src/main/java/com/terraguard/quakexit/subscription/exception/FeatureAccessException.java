package com.terraguard.quakexit.subscription.exception;

public class FeatureAccessException extends RuntimeException {
    private final String code;
    public FeatureAccessException(String code, String message) { super(message); this.code = code; }
    public String getCode() { return code; }
}