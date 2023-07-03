package org.winey.server.exception.model;

import org.winey.server.exception.Error;

public class NotFoundException extends CustomException {
    public NotFoundException(Error error, String message) {
        super(error, message);
    }
}
