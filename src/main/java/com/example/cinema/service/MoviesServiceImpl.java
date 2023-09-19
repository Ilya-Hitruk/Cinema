package com.example.cinema.service;

import com.example.cinema.dao.MoviesDao;
import com.example.cinema.model.dto.MoviesDto;
import com.example.cinema.model.entity.MoviesEntity;
import com.example.cinema.util.converter.MoviesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class MoviesServiceImpl implements MovieService {

    private final MoviesDao moviesDao;

    @Autowired
    public MoviesServiceImpl(MoviesDao moviesDao) {
        this.moviesDao = moviesDao;
    }

    @Override
    public List<MoviesDto> getAllMovies() {
        return moviesDao.findAll()
                .stream()
                .map(MoviesMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MoviesDto create(MoviesDto moviesDto) {
        if (moviesDao.findById(moviesDto.getId()).isPresent()) {
            throw new RuntimeException();
        }

        MoviesEntity entity = moviesDao.save(MoviesMapper.dtoToEntity(moviesDto));

        return MoviesMapper.entityToDto(entity);
    }

    @Override
    public MoviesDto getMovieById(int id) {
        Optional<MoviesEntity> optionalMoviesEntity = moviesDao.findById(id);
        if (optionalMoviesEntity.isEmpty()) {
            throw new RuntimeException();
        }

        MoviesEntity entity = optionalMoviesEntity.get();

        return MoviesMapper.entityToDto(entity);
    }

    @Override
    public List<String> getMovieGenres(int id) {
        Optional<MoviesEntity> optionalMoviesEntity = moviesDao.findById(id);
        if (optionalMoviesEntity.isEmpty()) {
            throw new RuntimeException();
        }
        return optionalMoviesEntity.get().getGenres();
    }


    //    @Override
//    public void update(int id, GenresDto genresDto) {
//
//    }
//
//    @Override
//    public void delete(int id) {
//
//    }
}
