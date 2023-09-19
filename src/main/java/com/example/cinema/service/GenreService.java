package com.example.cinema.service;

import com.example.cinema.model.dto.GenresDto;

import java.util.List;

public interface GenreService {
    List<GenresDto> getAllGenres();
    GenresDto create(GenresDto genresDto);
    GenresDto getGenreById(int id);
    void update(int id, GenresDto genresDto);
    void delete(int id);
    List<String> findMoviesByGenreId(int id);
}
