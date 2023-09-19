package com.example.cinema.dao;

import com.example.cinema.exception.DaoException;
import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.model.entity.MoviesEntity;
import com.example.cinema.util.connection.pool.ConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
public class MoviesDao {
    private final GenresDao genresDao;
    private final ConnectionManager connectionManager;

    @Autowired
    public MoviesDao(GenresDao genresDao, ConnectionManager connectionManager) {
        this.genresDao = genresDao;
        this.connectionManager = connectionManager;
    }

    private static final String SAVE_SQL = """
            INSERT INTO movies(title, release_date, director, duration_minutes, description)
            VALUES (?, ?, ?, ?, ?)
            """;


    private static final String UPDATE_SQL = """
            UPDATE movies
            SET title = ?,
                release_date = ?,
                director = ?,
                duration_minutes = ?,
                description = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM movies
            WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id,
            title,
            release_date,
            director,
            duration_minutes,
            description
            FROM movies
            WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id,
            title,
            release_date,
            director,
            duration_minutes,
            description
            FROM movies
            """;

    private static final String MOVIE_TO_GENRE = """
            INSERT INTO movie_to_genre (movie_id, genre_id)
            VALUES (?, ?)
            """;

    private static final String FIND_MOVIE_GENRES = """
            SELECT g.genre
            FROM genres g
            INNER JOIN movie_to_genre mtg on g.id = mtg.genre_id
            WHERE mtg.movie_id = ?
            """;


    public boolean delete(Integer id) {
        return false;
    }


    public MoviesEntity save(MoviesEntity entity) {
        try (var connection = connectionManager.get();

             //set autocommit(false)
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {


            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setDate(2, Date.valueOf(entity.getReleaseDate()));
            preparedStatement.setString(3, entity.getDirector());
            preparedStatement.setInt(4, entity.getDurationMinutes());
            preparedStatement.setString(5, entity.getDescription());
            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt("id"));
            }

            for (String genre : entity.getGenres()) {
                int genreId = findOrCreateGenreId(genre, connection);         //// is it possible to share connection to another method
                if (genreId > 0) {
                    linkMovieToGenre(connection, entity.getId(), genreId);
                }
            }
            return entity;

            // commit - rollback
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    public void update(MoviesEntity entity) {
        try (var connection = connectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, entity.getTitle());
            preparedStatement.setDate(2, Date.valueOf(entity.getReleaseDate()));
            preparedStatement.setString(3, entity.getDirector());
            preparedStatement.setInt(4, entity.getDurationMinutes());
            preparedStatement.setString(5, entity.getDescription());
            preparedStatement.setInt(6, entity.getId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    public Optional<MoviesEntity> findById(Integer id) {
        try (var connection = connectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();

            MoviesEntity moviesEntity = null;

            if (resultSet.next()) {
                moviesEntity = buildMoviesEntity(resultSet, connection);
            }

            return Optional.ofNullable(moviesEntity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<MoviesEntity> findAll() {
        try (var connection = connectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<MoviesEntity> movies = new ArrayList<>();
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                movies.add(buildMoviesEntity(resultSet, connection));
            }

            return movies;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private List<String> findMovieGenres(Connection connection, int movieId) {
        try {
            var preparedStatement = connection.prepareStatement(FIND_MOVIE_GENRES);
            preparedStatement.setInt(1, movieId);
            var resultSet = preparedStatement.executeQuery();
            List<String> movieGenres = new ArrayList<>();

            while (resultSet.next()) {
                movieGenres.add(resultSet.getString("genre"));
            }
            return movieGenres;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer findOrCreateGenreId(String genre, Connection connection) {
        Integer id = null;
        GenresEntity entity = genresDao.findByGenre(genre, connection);

        if (Objects.nonNull(entity)) {
            id = entity.getId();
        } else {
            GenresEntity genresEntity = new GenresEntity(null, genre);
            id = genresDao.save(genresEntity, connection).getId();
        }
        return id;
    }

    private void linkMovieToGenre(Connection connection, int movieId, int genreId) {
        try {
            var preparedStatement = connection.prepareStatement(MOVIE_TO_GENRE);

            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, genreId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private MoviesEntity buildMoviesEntity(ResultSet resultSet, Connection connection) throws SQLException {
        return new MoviesEntity(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getDate(3).toLocalDate(),
                resultSet.getString(4),
                resultSet.getInt(5),
                resultSet.getString(6),
                findMovieGenres(connection, resultSet.getInt(1))
        );
    }
}