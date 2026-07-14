package com.planemanagement.exception;

public class PlaneManagementException extends Exception {
    public PlaneManagementException(String message) {
        super(message);
    }

    public PlaneManagementException(String message, Throwable cause) {
        super(message, cause);
    }
}
