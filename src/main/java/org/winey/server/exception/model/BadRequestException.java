package org.winey.server.exception.model;

import org.winey.server.exception.Error;

public class BadRequestException extends CustomException {
    public BadRequestException(Error error, String message) {
        super(error, message);
    }
}
