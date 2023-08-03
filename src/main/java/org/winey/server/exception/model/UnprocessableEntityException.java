package org.winey.server.exception.model;

import org.winey.server.exception.Error;

public class UnprocessableEntityException  extends CustomException {
    public UnprocessableEntityException(Error error, String message) {
        super(error, message);
    }
}
