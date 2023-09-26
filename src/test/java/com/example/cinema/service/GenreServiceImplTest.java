package com.example.cinema.service;

import com.example.cinema.dao.GenresDao;
import com.example.cinema.exception.BadRequestException;
import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.util.mapper.GenresMapper;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {
    @Mock
    private GenresDao genresDao;
    @Mock
    private GenresMapper genresMapper;
    @InjectMocks
    private GenreServiceImpl genreService;

    @Test
    void getAllGenresShouldReturnAllGenres() {
        List<GenresEntity> genresEntities = List.of(
                new GenresEntity(1, "Adventure"),
                new GenresEntity(2, "Fantasy"));

        List<GenresDto> genresDtos = List.of(
                new GenresDto(1, "Adventure"),
                new GenresDto(2, "Fantasy")
        );

        doReturn(genresEntities).when(genresDao).findAll();
        List<GenresDto> actualResult = genreService.getAllGenres();

        assertThat(actualResult).hasSize(genresDtos.size());
        assertThat(actualResult).contains(genresDtos.get(0), genresDtos.get(1));

        verify(genresDao, times(1)).findAll();
    }

    @Test
    void getAllGenresShouldThrowExceptionNoGenresFound() {
        doReturn(Collections.emptyList()).when(genresDao).findAll();

        assertThatThrownBy(() -> genreService.getAllGenres())
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No genres found");

        verify(genresDao, times(1)).findAll();
    }

    @Test
    void getGenreByIdShouldReturnGenre() {
        GenresEntity genresEntity = getEntity();
        GenresDto genresDto = getDto();

        doReturn(genresEntity).when(genresDao).findById(genresEntity.getId());
        doReturn(genresDto).when(genresMapper).entityToDto(genresEntity);

        GenresDto actualResult = genreService.getGenreById(genresEntity.getId());

        assertThat(actualResult).isNotNull();
        assertThat(actualResult).isEqualTo(genresDto);

        verify(genresDao, times(1)).findById(genresEntity.getId());
        verify(genresMapper, times(1)).entityToDto(genresEntity);
    }

    @Test
    void getGenreByIdShouldThrowExceptionGenreDoesNotExist() {
        int fakeId = 0;

        doReturn(null).when(genresDao).findById(fakeId);

        assertThatThrownBy(() -> genreService.getGenreById(fakeId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Genre with id '%s' does not exist".formatted(fakeId));

        verify(genresDao, times(1)).findById(fakeId);
        verifyNoInteractions(genresMapper);
    }

    @Test
    void createShouldCreateGenre() {
        GenresEntity genresEntity = getEntity();
        GenresDto genresDto = getDto();

        doReturn(genresEntity).when(genresMapper).dtoToEntity(genresDto);
        doReturn(genresEntity).when(genresDao).save(genresEntity);
        doReturn(genresDto).when(genresMapper).entityToDto(genresEntity);

        GenresDto actualResult = genreService.create(genresDto);

        assertThat(actualResult).isEqualTo(genresDto);

        verify(genresDao, times(1)).save((GenresEntity) any());
        verify(genresMapper, times(1)).dtoToEntity(any());
        verify(genresMapper, times(1)).entityToDto(any());
    }

    @Test
    void createShouldThrowExceptionGenreAlreadyExist() {
        GenresEntity fakeEntity = getEntity();
        GenresDto fakeDto = getDto();

        doReturn(fakeEntity).when(genresMapper).dtoToEntity(fakeDto);
        doReturn(null).when(genresDao).save(fakeEntity);

        assertThatThrownBy(() -> genreService.create(fakeDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Genre with the title '" + fakeEntity.getTitle() + "' already exist");

        verify(genresDao, times(1)).save((GenresEntity) any());
        verify(genresMapper, times(1)).dtoToEntity(any());
        verify(genresMapper, times(0)).entityToDto(any());
    }

    @Test
    void updateShouldUpdateExistingGenre() {
        GenresEntity genresEntity = getEntity();
        GenresDto genresDto = getDto();

        doReturn(genresEntity).when(genresMapper).dtoToEntity(genresDto);
        doReturn(true).when(genresDao).update(genresEntity);

        genreService.update(genresDto.getId(), genresDto);

        verify(genresDao, times(1)).update(genresEntity);
        verify(genresMapper, times(1)).dtoToEntity(genresDto);
    }

    @Test
    void updateShouldThrowExceptionGenreDoesNotExists() {
        int fakeId = 0;
        GenresEntity genresEntity = getEntity();
        GenresDto genresDto = getDto();

        doReturn(genresEntity).when(genresMapper).dtoToEntity(genresDto);
        doReturn(false).when(genresDao).update(any());

        assertThatThrownBy(() -> genreService.update(fakeId, genresDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Genre with id '%s' does not exist".formatted(fakeId));

        verify(genresDao, times(1)).update(genresEntity);
        verify(genresMapper, times(1)).dtoToEntity(genresDto);
    }

    @Test
    void deleteShouldDeleteGenreById() {
        int idOfExistingGenre = 1;
        doReturn(true).when(genresDao).delete(idOfExistingGenre);

        genreService.delete(idOfExistingGenre);

        verify(genresDao, times(1)).delete(idOfExistingGenre);
    }

    @Test
    void deleteShouldThrowExceptionGenreDoesNotExist() {
        int fakeId = 0;
        doReturn(false).when(genresDao).delete(fakeId);

        assertThatThrownBy(() -> genreService.delete(fakeId))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Genre with id '%s' does not exist".formatted(fakeId));

        verify(genresDao, times(1)).delete(fakeId);
    }

    @Test
    void findMoviesByGenreIdShouldReturnListOfMovies() {
        int id = 1;
        List<String> expectedMovies = List.of("Inception", "Star Wars", "Dune");

        doReturn(expectedMovies).when(genresDao).findMoviesByGenre(id);

        List<String> actualMovies = genreService.findMoviesByGenreId(id);

        assertThat(actualMovies).containsAll(expectedMovies);

        verify(genresDao, times(1)).findMoviesByGenre(id);
    }

    @Test
    void findMoviesByGenreIdShouldThrowExceptionNoMoviesBySpecifiedGenreId() {
        int id = 1;
        List<String> noMoviesFound = Collections.emptyList();

        doReturn(noMoviesFound).when(genresDao).findMoviesByGenre(id);

        assertThatThrownBy(() -> genreService.findMoviesByGenreId(id))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("No movies found by specified genre");

        verify(genresDao, times(1)).findMoviesByGenre(id);
    }

    @NonNull
    private static GenresDto getDto() {
        return new GenresDto(1, "Adventure");
    }

    @NonNull
    private static GenresEntity getEntity() {
        return new GenresEntity(1, "Adventure");
    }
}