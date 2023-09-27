package com.example.cinema.service;

import com.example.cinema.dao.MoviesDao;
import com.example.cinema.exception.BadRequestException;
import com.example.cinema.model.dto.MoviesDto;
import com.example.cinema.model.entity.MoviesEntity;
import com.example.cinema.util.mapper.MoviesMapper;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoviesServiceImplTest {
    @Mock
    private MoviesDao moviesDao;
    @Mock
    private MoviesMapper moviesMapper;
    @InjectMocks
    private MoviesServiceImpl moviesService;

    @Test
    void getAllMoviesShouldReturnListOfMovies() {
        List<MoviesEntity> movies = getListOfMovies();

        doReturn(movies).when(moviesDao).findAll();
        List<MoviesDto> actual = moviesService.getAllMovies();

        assertThat(actual).isNotEmpty();
        verify(moviesDao, times(1)).findAll();
    }

    @Test
    void createShouldCreateMovie() {
        MoviesEntity moviesEntity = getEntity();
        MoviesDto moviesDto = getDto();

        doReturn(moviesEntity).when(moviesMapper).dtoToEntity(moviesDto);
        doReturn(moviesEntity).when(moviesDao).save(moviesEntity);
        doReturn(moviesDto).when(moviesMapper).entityToDto(moviesEntity);

        MoviesDto actual = moviesService.create(moviesDto);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(moviesDto);

        verify(moviesDao, times(1)).save(moviesEntity);
        verify(moviesMapper, times(1)).dtoToEntity(any());
        verify(moviesMapper, times(1)).entityToDto(any());
    }

    @Test
    void getMovieByIdShouldReturnMovie() {
        MoviesEntity moviesEntity = getEntity();
        MoviesDto moviesDto = getDto();

        doReturn(moviesEntity).when(moviesDao).findById(moviesDto.getId());
        doReturn(moviesDto).when(moviesMapper).entityToDto(moviesEntity);

        MoviesDto actual = moviesService.getMovieById(moviesDto.getId());

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(moviesDto);

        verify(moviesDao, times(1)).findById(moviesDto.getId());
        verify(moviesMapper, times(1)).entityToDto(moviesEntity);
    }

    @Test
    void getMovieByIdShouldThrowExceptionMovieDoesNotExist() {
        int fakeId = 0;

        doReturn(null).when(moviesDao).findById(fakeId);

        assertThatThrownBy(() -> moviesService.getMovieById(fakeId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Movie with id '%s' does not exist".formatted(fakeId));

        verify(moviesDao, times(1)).findById(fakeId);
    }

    @Test
    void getMovieGenres() {
        MoviesEntity moviesEntity = getEntity();

        doReturn(moviesEntity).when(moviesDao).findById(moviesEntity.getId());
        List<String> actual = moviesService.getMovieGenres(moviesEntity.getId());

        assertThat(actual).containsAll(moviesEntity.getGenres());
        verify(moviesDao, times(1)).findById(moviesEntity.getId());
    }

    @Test
    void updateShouldUpdateMovieBySpecifiedId() {
        int id = 1;
        MoviesEntity moviesEntity = getEntity();
        MoviesDto moviesDto = getDto();

        doReturn(moviesEntity).when(moviesMapper).dtoToEntity(moviesDto);
        doReturn(true).when(moviesDao).update(moviesEntity);

        moviesService.update(id, moviesDto);

        verify(moviesMapper, times(1)).dtoToEntity(moviesDto);
        verify(moviesDao, times(1)).update(moviesEntity);
    }

    @Test
    void updateShouldThrowExceptionMovieDoesNotExists() {
        int fakeId = 0;
        MoviesDto moviesDto = getDto();
        MoviesEntity moviesEntity = getEntity();

        doReturn(moviesEntity).when(moviesMapper).dtoToEntity(moviesDto);
        doReturn(false).when(moviesDao).update(moviesEntity);

        assertThatThrownBy(() -> moviesService.update(fakeId, moviesDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Movie with id '%s' does not exist".formatted(fakeId));

        verify(moviesMapper, times(1)).dtoToEntity(moviesDto);
        verify(moviesDao, times(1)).update(moviesEntity);
    }

    @Test
    void deleteShouldDeleteMovieById() {
        int id = 1;

        doReturn(true).when(moviesDao).delete(id);
        moviesService.delete(id);

        verify(moviesDao, times(1)).delete(id);
    }

    @Test
    void deleteShouldThrowExceptionMovieDoesNotExist() {
        int fakeId = 0;

        doReturn(false).when(moviesDao).delete(fakeId);
        assertThatThrownBy(() -> moviesService.delete(fakeId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Movie with id '%s' does not exist".formatted(fakeId));

        verify(moviesDao, times(1)).delete(fakeId);
    }

    @NonNull
    private static MoviesDto getDto() {
        return new MoviesDto(
                1,
                "Star Wars",
                LocalDate.of(1999, 5, 19),
                "George Lukas",
                136,
                "The first Star Wars episode",
                List.of("Action",
                        "Fantasy",
                        "Adventure")
        );
    }

    @NonNull
    private static MoviesEntity getEntity() {
        return new MoviesEntity(
                1,
                "Star Wars",
                LocalDate.of(1999, 5, 19),
                "George Lukas",
                136,
                "The first Star Wars episode",
                List.of("Action",
                        "Fantasy",
                        "Adventure")
        );
    }

    @NonNull
    private static List<MoviesEntity> getListOfMovies() {
        return List.of(new MoviesEntity(
                1,
                "Star Wars",
                LocalDate.of(1999, 5, 19),
                "George Lukas",
                136,
                "The first Star Wars episode",
                List.of("Action",
                        "Fantasy",
                        "Adventure")
        ), new MoviesEntity(
                2,
                "Game of Thrones. Season 1",
                LocalDate.of(2011, 4, 17),
                "David Benioff",
                50,
                "First season of series",
                List.of("Action",
                        "Medieval",
                        "Adventure",
                        "Drama")
        ), new MoviesEntity(
                3,
                "The Avengers",
                LocalDate.of(2012, 4, 11),
                "Joss Whedon",
                143,
                "Marvel Avengers",
                List.of("Action",
                        "Superheroes",
                        "Adventure",
                        "Fighting")
        ));
    }
}