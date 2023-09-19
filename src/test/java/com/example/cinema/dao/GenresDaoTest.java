package com.example.cinema.dao;

import com.example.cinema.exception.DaoException;
import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.util.connection.pool.ConnectionManager;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class GenresDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.4"
    )
            .withInitScript("init.sql")
            .withDatabaseName("cinema_repository")
            .withAccessToHost(true);

    static GenresDao genresDao;

    @BeforeAll
    static void beforeAll() {

        postgres.start();

        var connectionManager = new ConnectionManager(
                postgres.getPassword(),
                postgres.getUsername(),
                postgres.getJdbcUrl(),
                10);
        connectionManager.initConnectionPool();
        genresDao = new GenresDao(connectionManager);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {

    }


    @Test
    void findMoviesByGenre() {
    }

    @Test
    void delete() {
    }

    @Test
    void save() {
        GenresEntity entityForSaving = new GenresEntity(null, "Musical");
        GenresEntity actual = genresDao.save(entityForSaving);

        Assertions.assertEquals(new GenresEntity(actual.getId(), "Musical"), actual);
    }

    //  AssertJ DB

    @Test
    void update() {
        GenresEntity entity = new GenresEntity(2, "Adventure");
        genresDao.update(entity);
        Assertions.assertEquals(entity, genresDao.findById(entity.getId()));
    }

    @Test
    void findByIdShouldReturnGenre() {
        GenresEntity actual = genresDao.findById(2);
        GenresEntity expected = new GenresEntity(2, "Adventure");

        assertEquals(expected, actual);
    }

//    @Test
//    void findByIdShouldThrowException() {
//        Assertions.assertThrows(DaoException.class,() -> genresDao.findById(0));
//    }

    @Test
    void findByGenre() {
//        GenresEntity actual = genresDao.findByGenre("Adventure").get();
//        GenresEntity expected = new GenresEntity(2, "Adventure");
//        assertEquals(expected, actual);
    }


    @Test
    void findAll() {
        Assertions.assertEquals(8,genresDao.findAll().size());
    }
}