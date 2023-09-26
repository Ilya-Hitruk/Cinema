package com.example.cinema.util.mapper;

import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.entity.GenresEntity;
import org.springframework.stereotype.Component;

@Component
public class GenresMapper{

    public GenresDto entityToDto(GenresEntity genresEntity) {
        return new GenresDto(genresEntity.getId(), genresEntity.getTitle());
    }

    public GenresEntity dtoToEntity(GenresDto genresDto) {
        return new GenresEntity(genresDto.getId(), genresDto.getTitle());
    }
}
