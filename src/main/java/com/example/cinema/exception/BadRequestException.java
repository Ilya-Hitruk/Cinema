package com.example.cinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public static BadRequestException genreAlreadyExists(String name) {
        return new BadRequestException("Genre with name '" + name + "' already exists");
    }

    public static BadRequestException genreDoesNotExists(long id) {
        return new BadRequestException("Genre with id '%s' does not exists".formatted(id));
    }
}