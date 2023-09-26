package com.example.cinema.util.mapper;

import com.example.cinema.model.dto.MoviesDto;
import com.example.cinema.model.entity.MoviesEntity;
import org.springframework.stereotype.Component;

@Component
public class MoviesMapper {

    public MoviesDto entityToDto(MoviesEntity moviesEntity) {
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

    public MoviesEntity dtoToEntity(MoviesDto moviesDto) {
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
