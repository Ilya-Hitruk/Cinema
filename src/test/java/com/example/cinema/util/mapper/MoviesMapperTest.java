package com.example.cinema.util.mapper;

import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.dto.MoviesDto;
import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.model.entity.MoviesEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MoviesMapperTest {

    private final MoviesMapper moviesMapper = new MoviesMapper();
    @Test
    void entityToDto() {
        MoviesEntity entity = new MoviesEntity(
                1,
                "Star Wars",
                LocalDate.of(1999, 5, 19),
                "George Lukas",
                136,
                "The first Star Wars episode",
                Collections.emptyList()
        );

        MoviesDto actualResult = moviesMapper.entityToDto(entity);
        MoviesDto expectedResult = new MoviesDto(
                1,
                "Star Wars",
                LocalDate.of(1999, 5, 19),
                "George Lukas",
                136,
                "The first Star Wars episode",
                Collections.emptyList());

        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void dtoToEntity() {
        MoviesDto dto = new MoviesDto(
                1,
                "Star Wars",
                LocalDate.of(1999, 5, 19),
                "George Lukas",
                136,
                "The first Star Wars episode",
                Collections.emptyList());

        MoviesEntity actualResult = moviesMapper.dtoToEntity(dto);
        MoviesEntity expectedResult = new MoviesEntity(
                1,
                "Star Wars",
                LocalDate.of(1999, 5, 19),
                "George Lukas",
                136,
                "The first Star Wars episode",
                Collections.emptyList());

        assertThat(actualResult).isEqualTo(expectedResult);
    }
}