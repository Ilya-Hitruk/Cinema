package com.example.cinema.exception;

public class DaoException extends RuntimeException {
    public DaoException(Throwable throwable) {
        System.out.println(throwable);
    }
}
