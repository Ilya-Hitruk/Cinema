package com.example.cinema.service;

import com.example.cinema.dao.GenresDao;
import com.example.cinema.exception.BadRequestException;
import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.util.mapper.GenresMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GenreServiceImpl implements GenreService {
    private final GenresDao genresDao;
    private final GenresMapper genresMapper;

    @Autowired
    public GenreServiceImpl(GenresDao genresDao, GenresMapper genresMapper) {
        this.genresDao = genresDao;
        this.genresMapper = genresMapper;
    }

    @Override
    public List<GenresDto> getAllGenres() {
        List<GenresEntity> allGenres = genresDao.findAll();

        if (allGenres.isEmpty()) {
            throw BadRequestException.noGenresFound();
        }
        return allGenres.stream()
                .map(g -> new GenresDto(g.getId(), g.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public GenresDto getGenreById(int id) {
        GenresEntity entity = genresDao.findById(id);

        if (Objects.isNull(entity)) {
            throw BadRequestException.genreDoesNotExists(id);
        }

        return genresMapper.entityToDto(entity);
    }

    @Override
    public GenresDto create(GenresDto genresDto) {
        GenresEntity entity = genresDao.save(genresMapper.dtoToEntity(genresDto));

        if (Objects.isNull(entity)) {
            throw BadRequestException.genreAlreadyExists(genresDto.getTitle());
        }

        return genresMapper.entityToDto(entity);
    }

    @Override
    public void update(int id, GenresDto genresDto) {
        genresDto.setId(id);

        boolean isUpdated = genresDao.update(genresMapper.dtoToEntity(genresDto));

        if (!isUpdated) {
            throw BadRequestException.genreDoesNotExists(id);
        }
    }

    @Override
    public void delete(int id) {
        boolean isDeleted = genresDao.delete(id);

        if (!isDeleted) {
            throw BadRequestException.genreDoesNotExists(id);
        }
    }

    @Override
    public List<String> findMoviesByGenreId(int id) {
        List<String> movies = genresDao.findMoviesByGenre(id);

        if (movies.isEmpty()) {
            throw BadRequestException.noMoviesBySpecifiedGenreId();
        }

        return movies;
    }
}