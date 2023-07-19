package org.winey.server.exception.model;

import org.winey.server.exception.Error;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(Error error, String message) {
        super(error, message);
    }
}
