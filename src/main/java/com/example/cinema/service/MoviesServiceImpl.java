package com.example.cinema.service;

import com.example.cinema.dao.MoviesDao;
import com.example.cinema.exception.BadRequestException;
import com.example.cinema.model.dto.MoviesDto;
import com.example.cinema.model.entity.MoviesEntity;
import com.example.cinema.util.mapper.MoviesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MoviesServiceImpl implements MovieService {
    private final MoviesDao moviesDao;
    private final MoviesMapper moviesMapper;

    @Autowired
    public MoviesServiceImpl(MoviesDao moviesDao, MoviesMapper moviesMapper) {
        this.moviesDao = moviesDao;
        this.moviesMapper = moviesMapper;
    }

    @Override
    public List<MoviesDto> getAllMovies() {
        return moviesDao.findAll()
                .stream()
                .map(moviesMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MoviesDto create(MoviesDto moviesDto) {
        MoviesEntity entity = moviesDao.save(moviesMapper.dtoToEntity(moviesDto));

        return moviesMapper.entityToDto(entity);
    }

    @Override
    public MoviesDto getMovieById(int id) {
        MoviesEntity moviesEntity = moviesDao.findById(id);

        if (Objects.isNull(moviesEntity)) {
            throw BadRequestException.movieDoesNotExists(id);
        }

        return moviesMapper.entityToDto(moviesEntity);
    }

    @Override
    public List<String> getMovieGenres(int id) {
        MoviesEntity moviesEntity = moviesDao.findById(id);// !!!!!!!!!!!!
        return moviesEntity.getGenres();
    }


    @Override
    public void update(int id, MoviesDto moviesDto) {
        moviesDto.setId(id);

        boolean isUpdated = moviesDao.update(moviesMapper.dtoToEntity(moviesDto));

        if (!isUpdated) {
            throw BadRequestException.movieDoesNotExists(moviesDto.getId());
        }
    }

    @Override
    public void delete(int id) {
        boolean isDeleted = moviesDao.delete(id);

        if (!isDeleted) {
            throw BadRequestException.movieDoesNotExists(id);
        }
    }
}