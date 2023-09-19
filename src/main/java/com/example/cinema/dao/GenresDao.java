package com.example.cinema.dao;

import com.example.cinema.exception.DaoException;
import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.util.connection.pool.ConnectionManager;
import com.example.cinema.util.connection.pool.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class GenresDao {
    private final ConnectionManager connectionManager;

    @Autowired
    public GenresDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    // pass connection manager to constructor
    private static final String SAVE_SQL = """
            INSERT INTO genres(genre)
            VALUES (?)
            """;


    private static final String UPDATE_SQL = """
            UPDATE genres
            SET genre = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM genres
            WHERE id = ?
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT id,
            genre
            FROM genres
            WHERE id = ?
            """;

    private static final String FIND_BY_GENRE_SQL = """
            SELECT id,
            genre
            FROM genres
            WHERE genre = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id,
            genre
            FROM genres
            """;

    private static final String FIND_MOVIES_BY_GENRE_SQL = """
            SELECT m.title
            FROM movies m
            INNER JOIN movie_to_genre mtg on m.id = mtg.movie_id
            WHERE mtg.genre_id = ?;
            """;


    public boolean delete(Integer id) {
        try (var connection = connectionManager.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    public GenresEntity save(GenresEntity entity) {
        try (var connection = connectionManager.get()) {

            return save(entity, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    public boolean update(GenresEntity entity) {
        try (var connection = connectionManager.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {


            preparedStatement.setString(1, entity.getGenre());
            preparedStatement.setInt(2, entity.getId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    public GenresEntity findById(int id) {
        try (var connection = connectionManager.get()) {

            return findById(id, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private GenresEntity findById(int id, Connection connection) {
        GenresEntity genresEntity = null;

        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                genresEntity = buildGenreEntity(resultSet);
            }

            return genresEntity;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }


    public GenresEntity findByGenre(String genre) {
        try (var connection = connectionManager.get()) {

            return findByGenre(genre, connection);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<GenresEntity> findAll() {
        try (var connection = connectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<GenresEntity> genresEntitiesList = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                genresEntitiesList.add(buildGenreEntity(resultSet));
            }
            return genresEntitiesList;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<String> findMoviesByGenre(int id) {

        List<String> movies = new ArrayList<>();

        try (var connection = connectionManager.get();
             var preparedStatement = connection.prepareStatement(FIND_MOVIES_BY_GENRE_SQL)) {

            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                movies.add(resultSet.getString("title"));
            }

            return movies;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    GenresEntity save(GenresEntity entity, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, entity.getGenre());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt("id"));
            } else {
                return null;
            }

            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    GenresEntity findByGenre(String genre, Connection connection) {
        GenresEntity genresEntity = null;

        try (var preparedStatement = connection.prepareStatement(FIND_BY_GENRE_SQL)) {
            preparedStatement.setString(1, genre);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                genresEntity = buildGenreEntity(resultSet);
            }

            return genresEntity;

        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private static GenresEntity buildGenreEntity(ResultSet resultSet) throws SQLException {
        return new GenresEntity(resultSet.getInt("id"), resultSet.getString("genre"));
    }
}