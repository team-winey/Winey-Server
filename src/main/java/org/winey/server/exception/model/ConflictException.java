package org.winey.server.exception.model;

import org.winey.server.exception.Error;

public class ConflictException extends CustomException {
    public ConflictException(Error error, String message) {
        super(error, message);
    }
}
