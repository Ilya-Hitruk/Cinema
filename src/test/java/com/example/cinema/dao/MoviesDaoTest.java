package com.example.cinema.dao;

import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.model.entity.MoviesEntity;
import com.example.cinema.util.connection.pool.ConnectionManager;
import lombok.NonNull;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

@Testcontainers
@SpringBootTest
class MoviesDaoTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.4"
    )
            .withInitScript("init.sql")
            .withDatabaseName("cinema_repository")
            .withAccessToHost(true);

    static MoviesDao moviesDao;
    static GenresDao genresDao;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        var connectionManager = new ConnectionManager(
                postgres.getPassword(),
                postgres.getUsername(),
                postgres.getJdbcUrl(),
                10);
        connectionManager.initConnectionPool();
        genresDao = new GenresDao(connectionManager);
        moviesDao = new MoviesDao(genresDao, connectionManager);
    }

    @Test
    void saveShouldSaveMovie() {
        MoviesEntity moviesEntity = getEntity();

        MoviesEntity actual = moviesDao.save(moviesEntity);

        assertThat(actual).isEqualTo(moviesEntity);
    }

    @Test
    void findByIdShouldReturnMovieFoundedById() {
        MoviesEntity saved = moviesDao.save(getEntity());

        MoviesEntity actual = moviesDao.findById(saved.getId());

        assertThat(actual).isEqualTo(saved);
    }

    @Test
    void findByIdShouldReturnNullCauseNoMovieFoundBySpecifiedId() {
        int fakeId = 0;

        MoviesEntity actual = moviesDao.findById(fakeId);

        assertThat(actual).isNull();
    }

    @Test
    void findAllShouldReturnAllExistingMovies() {                              // ???????????????????
        List<MoviesEntity> moviesEntities = getListOfMovies();
        moviesDao.save(moviesEntities);

        List<MoviesEntity> actual = moviesDao.findAll();

        assertThat(actual).hasSize(3);
        assertThat(actual).containsAll(moviesEntities);
    }

    @Test
    void updateShouldUpdateMovieAndReturnTrue() {
        MoviesEntity forUpdate = moviesDao.save(getEntity());
        String newDescription = "New description";
        forUpdate.setDescription(newDescription);

        boolean actual = moviesDao.update(forUpdate);
        MoviesEntity updatedEntity = moviesDao.findById(forUpdate.getId());

        Assertions.assertTrue(actual);
        assertThat(updatedEntity.getDescription()).isEqualTo(newDescription);
    }

    @Test
    void updateShouldNotUpdateMovieAndReturnFalseCauseNoMovieFoundBySpecifiedId() {
        int fakeId = 0;
        MoviesEntity fakeEntity = getEntity();
        fakeEntity.setId(fakeId);

        boolean actual = moviesDao.update(fakeEntity);
        MoviesEntity shouldNotExist = moviesDao.findById(fakeId);

        Assertions.assertFalse(actual);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldDeleteMovieAndReturnTrue() {
        MoviesEntity saved = moviesDao.save(getEntity());

        boolean actual = moviesDao.delete(saved.getId());
        MoviesEntity shouldNotExist = moviesDao.findById(saved.getId());

        Assertions.assertTrue(actual);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldNotDeleteAndReturnFalseCauseNoMovieFoundBySpecifiedId() {
        int fakeId = 0;

        boolean actual = moviesDao.delete(fakeId);
        MoviesEntity shouldNotExist = moviesDao.findById(fakeId);

        Assertions.assertFalse(actual);
        assertThat(shouldNotExist).isNull();
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
                        "Adventure",
                        "Fantasy")
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
                        "Adventure",
                        "Fantasy")
        ), new MoviesEntity(
                2,
                "Game of Thrones. Season 1",
                LocalDate.of(2011, 4, 17),
                "David Benioff",
                50,
                "First season of series",
                List.of("Action",
                        "Adventure",
                        "Medieval",
                        "Drama")
        ), new MoviesEntity(
                3,
                "The Avengers",
                LocalDate.of(2012, 4, 11),
                "Joss Whedon",
                143,
                "Marvel Avengers",
                List.of("Action",
                        "Adventure",
                        "Superheroes",
                        "Fighting")
        ));
    }
}