package com.example.cinema.util.converter;

import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.entity.GenresEntity;

public class GenresMapper {
    private GenresMapper(){}
    public static GenresDto entityToDto(GenresEntity genresEntity) {
        return new GenresDto(genresEntity.getId(), genresEntity.getGenre());
    }

    public static GenresEntity dtoToEntity(GenresDto genresDto) {
        return new GenresEntity(genresDto.getId(), genresDto.getGenre());
    }
}
