package com.example.cinema.util.mapper;


import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.entity.GenresEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenresMapperTest {
    private final GenresMapper genresMapper = new GenresMapper();
    @Test
    void entityToDto() {
        GenresEntity entity = new GenresEntity(1, "Fantasy");

        GenresDto actualResult = genresMapper.entityToDto(entity);
        GenresDto expectedResult = new GenresDto(1, "Fantasy");

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void dtoToEntity() {
        GenresDto dto = new GenresDto(1, "Adventure");

        GenresEntity actualResult = genresMapper.dtoToEntity(dto);
        GenresEntity expectedResult = new GenresEntity(1, "Adventure");

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}