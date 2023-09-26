package com.example.cinema.service;

import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.dto.MoviesDto;

import java.util.List;

public interface MovieService {
    List<MoviesDto> getAllMovies();

    MoviesDto create(MoviesDto moviesDto);

    MoviesDto getMovieById(int id);

    List<String> getMovieGenres(int id);

    void update(int id, MoviesDto moviesDto);

    void delete(int id);
}