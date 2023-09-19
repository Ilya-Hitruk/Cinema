package com.example.cinema.util.converter;

import com.example.cinema.model.dto.MoviesDto;
import com.example.cinema.model.entity.MoviesEntity;

public class MoviesMapper {

    public static MoviesDto entityToDto(MoviesEntity moviesEntity) {
        return new MoviesDto(
                moviesEntity.getId(),
                moviesEntity.getTitle(),
                moviesEntity.getReleaseDate(),
                moviesEntity.getDirector(),
                moviesEntity.getDurationMinutes(),
                moviesEntity.getDescription(),
                moviesEntity.getGenres()
        );
    }

    public static MoviesEntity dtoToEntity(MoviesDto moviesDto) {
        return new MoviesEntity(
                moviesDto.getId(),
                moviesDto.getTitle(),
                moviesDto.getReleaseDate(),
                moviesDto.getDirector(),
                moviesDto.getDurationMinutes(),
                moviesDto.getDescription(),
                moviesDto.getGenres()
        );
    }
}
