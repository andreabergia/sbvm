package com.andreabergia.sbvm;

public class InvalidProgramException extends RuntimeException {
    public InvalidProgramException(String message) {
        super(message);
    }
}
