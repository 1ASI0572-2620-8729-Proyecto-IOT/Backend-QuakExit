package com.terraguard.quakexit.common.exception;

public final class ApiExceptions {
    private ApiExceptions() {}
    public static class ResourceNotFoundException extends RuntimeException { public ResourceNotFoundException(String message) { super(message); } }
    public static class DuplicateResourceException extends RuntimeException { public DuplicateResourceException(String message) { super(message); } }
    public static class BusinessRuleException extends RuntimeException { public BusinessRuleException(String message) { super(message); } }
    public static class InvalidCredentialsException extends RuntimeException { public InvalidCredentialsException() { super("Credenciales invalidas"); } }
}
