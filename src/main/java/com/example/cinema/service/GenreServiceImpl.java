package com.example.cinema.service;

import com.example.cinema.dao.GenresDao;
import com.example.cinema.exception.BadRequestException;
import com.example.cinema.model.dto.GenresDto;
import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.util.converter.GenresMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GenreServiceImpl implements GenreService {
    private final GenresDao genresDao;

    @Autowired
    public GenreServiceImpl(GenresDao genresDao) {
        this.genresDao = genresDao;
    }

    @Override
    public List<GenresDto> getAllGenres() {
        return genresDao.findAll().stream()
                .map(g -> new GenresDto(g.getId(), g.getGenre()))
                .collect(Collectors.toList());
    }

    @Override
    public GenresDto getGenreById(int id) {
        GenresEntity entity = genresDao.findById(id);

        if (Objects.isNull(entity)) {
            throw BadRequestException.genreDoesNotExists(id);
        }

        return GenresMapper.entityToDto(entity);
    }

    @Override
    public GenresDto create(GenresDto genresDto) {
        GenresEntity entity = genresDao.save(GenresMapper.dtoToEntity(genresDto));

        if (Objects.isNull(entity)) {
            throw BadRequestException.genreAlreadyExists(genresDto.getGenre());
        }

        return GenresMapper.entityToDto(entity);
    }

    @Override
    public void update(int id, GenresDto genresDto) {
        boolean isUpdated = genresDao.update(GenresMapper.dtoToEntity(genresDto));
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
        return genresDao.findMoviesByGenre(id);
    }
}