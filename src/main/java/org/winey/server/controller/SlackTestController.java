package org.winey.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.winey.server.common.dto.ApiResponse;

@RestController
@RequestMapping("/test")
public class SlackTestController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse test() {
        throw new IllegalArgumentException();
    }
}
