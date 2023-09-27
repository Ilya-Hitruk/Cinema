package com.example.cinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public static BadRequestException genreAlreadyExists(String name) {
        return new BadRequestException("Genre with the title '" + name + "' already exist");
    }

    public static BadRequestException genreDoesNotExists(long id) {
        return new BadRequestException("Genre with id '%s' does not exist".formatted(id));
    }

    public static BadRequestException noGenresFound() {
        return new BadRequestException("No genres found");
    }

    public static BadRequestException noMoviesBySpecifiedGenreId() {
        return new BadRequestException("No movies found by specified genre");
    }

    public static BadRequestException movieAlreadyExists(String name) {
        return new BadRequestException("Movie with the title '" + name + "' already exist");
    }

    public static BadRequestException movieDoesNotExists(long id) {
        return new BadRequestException("Movie with id '%s' does not exist".formatted(id));
    }

    public static BadRequestException noMoviesFound() {
        return new BadRequestException("No movies found");
    }

}