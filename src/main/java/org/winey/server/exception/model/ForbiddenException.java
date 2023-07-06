package org.winey.server.exception.model;

import org.winey.server.exception.Error;

public class ForbiddenException extends CustomException{
    public ForbiddenException(Error error, String message) {
        super(error, message);
    }

}
