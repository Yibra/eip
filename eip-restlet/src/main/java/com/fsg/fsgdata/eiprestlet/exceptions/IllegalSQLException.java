package com.fsg.fsgdata.eiprestlet.exceptions;

public class IllegalSQLException extends Exception {
    public IllegalSQLException() {
    }

    public IllegalSQLException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return String.format("{\"failed\":\"IllegalSQLException\", \"message\":\"%s\"}", this.getMessage());
    }
}