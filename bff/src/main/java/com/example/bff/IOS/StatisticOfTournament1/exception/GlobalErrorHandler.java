// src/main/java/com/example/bff/exception/GlobalErrorHandler.java
package com.example.bff.IOS.StatisticOfTournament1.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorVm handleRemoteCallFailure(ResponseStatusException ex) {
        return new ErrorVm("COMPETITION_SERVICE_ERROR", ex.getReason());
    }

    public record ErrorVm(String code, String message) { }
}
