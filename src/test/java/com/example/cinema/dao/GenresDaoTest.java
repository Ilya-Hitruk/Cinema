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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//  AssertJ DB
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
//    static MoviesDao moviesDao;

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
    }


    @Test
    void saveShouldSaveGenre() {
        GenresEntity firstExpected = new GenresEntity("Adventure");
        GenresEntity secondExpected = new GenresEntity("Fantasy");


        GenresEntity firstActual = genresDao.save(firstExpected);
        GenresEntity secondActual = genresDao.save(secondExpected);

        assertThat(firstActual).isEqualTo(firstExpected);
        assertThat(secondActual).isEqualTo(secondExpected);
    }

    @Test
    void findMoviesByGenre() {

    }

    @Test
    void findAllShouldReturnAllExistingGenres() {
        List<GenresEntity> expected = getListOfGenres();
        genresDao.save(expected);
        List<GenresEntity> actual = genresDao.findAll();

        assertThat(actual).hasSize(expected.size());
        assertThat(actual).containsAll(expected);
    }

    @Test
    void findByIdShouldReturnGenre() {
        GenresEntity saved = genresDao.save(new GenresEntity("Adventure"));

        GenresEntity actual = genresDao.findById(saved.getId());

        assertThat(actual).isEqualTo(saved);
    }

    @Test
    void findByGenreTitleShouldReturnGenre() {
        GenresEntity saved = genresDao.save(new GenresEntity("Adventure"));

        GenresEntity actual = genresDao.findByGenreTitle(saved.getTitle());

        assertThat(actual).isEqualTo(saved);
    }

    @Test
    void findByIdShouldReturnNullCauseNoSuchGenreFoundBySpecifiedId() {
        int fakeId = 0;

        GenresEntity actual = genresDao.findById(fakeId);

        assertThat(actual).isNull();
    }

    @Test
    void findByGenreTitleShouldReturnNullCauseNoSuchTitleFound() {
        String fakeTitle = "fake";

        GenresEntity actual = genresDao.findByGenreTitle(fakeTitle);

        assertThat(actual).isNull();
    }

    @Test
    void updateShouldUpdateGenreAndReturnTrue() {
        GenresEntity saved = genresDao.save(new GenresEntity("Adventure"));
        GenresEntity forUpdate = new GenresEntity(saved.getId(), "Fantasy");

        boolean actual = genresDao.update(forUpdate);
        String updatedTitle = genresDao.findById(forUpdate.getId()).getTitle();

        Assertions.assertTrue(actual);
        assertThat(forUpdate.getTitle()).isEqualTo(updatedTitle);
    }

    @Test
    void updateShouldNotUpdateGenreAndReturnFalseCauseNoSuchEntityFound() {
        GenresEntity fakeEntity = new GenresEntity(0, "fake");

        boolean actual = genresDao.update(fakeEntity);
        GenresEntity shouldNotExist = genresDao.findById(fakeEntity.getId());

        Assertions.assertFalse(actual);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldDeleteGenre() {
        GenresEntity saved = genresDao.save(new GenresEntity("Adventure"));

        boolean actual = genresDao.delete(saved.getId());

        GenresEntity shouldNotExist = genresDao.findById(saved.getId());

        Assertions.assertTrue(actual);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldReturnFalseCauseNoSuchIdFound() {
        int fakeId = 0;

        boolean actual = genresDao.delete(fakeId);
        GenresEntity shouldNotExist = genresDao.findById(fakeId);

        Assertions.assertFalse(actual);
        assertThat(shouldNotExist).isNull();
    }

    @NonNull
    private static List<GenresEntity> getListOfGenres() {
        return List.of(
                new GenresEntity("Adventure"),
                new GenresEntity("Fantasy"),
                new GenresEntity("Thriller")
        );
    }
}