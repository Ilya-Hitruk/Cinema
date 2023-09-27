package com.example.cinema.dao;

import com.example.cinema.model.entity.MoviesEntity;
import com.example.cinema.util.connection.pool.ConnectionManager;
import lombok.NonNull;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;

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
    static Source dataSource;
    static Table moviesTable;

    @BeforeEach
    void setUp() {
        postgres.start();

        var connectionManager = new ConnectionManager(
                postgres.getPassword(),
                postgres.getUsername(),
                postgres.getJdbcUrl(),
                10);
        connectionManager.initConnectionPool();

        genresDao = new GenresDao(connectionManager);

        moviesDao = new MoviesDao(genresDao, connectionManager);

        dataSource = new Source(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        moviesTable = new Table(dataSource, "movies");
    }

    @AfterEach
    void tearDown() {
        postgres.stop();
    }

    @Test
    void saveShouldSaveMovieAndCheckSavedEntityInDB() {
        MoviesEntity expectedEntity = getEntity();

        assertThat(moviesTable).exists();

        MoviesEntity actualEntity = moviesDao.save(expectedEntity);

        assertThat(moviesTable).hasNumberOfRows(1);

        assertThat(moviesTable).column("title")
                        .value().isEqualTo(expectedEntity.getTitle());

        assertThat(moviesTable).row(0)
                        .value().isEqualTo(actualEntity.getId())
                        .value().isEqualTo(expectedEntity.getTitle())
                        .value().isEqualTo(expectedEntity.getReleaseDate())
                        .value().isEqualTo(expectedEntity.getDirector())
                        .value().isEqualTo(expectedEntity.getDurationMinutes())
                        .value().isEqualTo(expectedEntity.getDescription());

        assertThat(actualEntity).isEqualTo(expectedEntity);
    }

    @Test
    void findByIdShouldReturnMovieAndCheckExistingEntityInDB() {
        MoviesEntity expectedEntity = getEntity();

        int idOfSavedEntity = moviesDao.save(expectedEntity).getId();

        assertThat(moviesTable).hasNumberOfRows(1);

        assertThat(moviesTable).row(0)
                .value().isEqualTo(idOfSavedEntity)
                .value().isEqualTo(expectedEntity.getTitle())
                .value().isEqualTo(expectedEntity.getReleaseDate())
                .value().isEqualTo(expectedEntity.getDirector())
                .value().isEqualTo(expectedEntity.getDurationMinutes())
                .value().isEqualTo(expectedEntity.getDescription());

        MoviesEntity actualEntity = moviesDao.findById(idOfSavedEntity);

        assertThat(actualEntity).isEqualTo(expectedEntity);
    }

    @Test
    void findByIdShouldReturnNullCauseNoSuchIdFoundAndCheckThatSpecifiedEntityIsNotExistInDB() {
        int fakeId = 0;

        assertThat(moviesTable).exists();
        assertThat(moviesTable).hasNumberOfRows(0);

        MoviesEntity actualEntity = moviesDao.findById(fakeId);

        assertThat(actualEntity).isNull();
    }

    @Test
    void findAllShouldReturnAllExistingMoviesAndCheckExistingEntitiesInDB() {                              // ???????????????????
        List<MoviesEntity> expectedListOfMovies = getListOfMovies();
        List<MoviesEntity> savedList = moviesDao.save(expectedListOfMovies);

        assertThat(moviesTable).hasNumberOfRows(expectedListOfMovies.size());

        for (MoviesEntity savedEntity: savedList) {
            MoviesEntity expectedEntity =
                    expectedListOfMovies.get(savedEntity.getId() - 1);

            assertThat(moviesTable).row(savedEntity.getId() - 1)
                    .value().isEqualTo(savedEntity.getId())
                    .value().isEqualTo(expectedEntity.getTitle())
                    .value().isEqualTo(expectedEntity.getReleaseDate())
                    .value().isEqualTo(expectedEntity.getDirector())
                    .value().isEqualTo(expectedEntity.getDurationMinutes())
                    .value().isEqualTo(expectedEntity.getDescription());
        }

        List<MoviesEntity> actualListOfMovies = moviesDao.findAll();

        assertThat(actualListOfMovies).hasSize(3);
        assertThat(actualListOfMovies).containsAll(expectedListOfMovies);
    }

    @Test
    void updateShouldUpdateMovieReturnTrueAndCheckUpdatedEntityInDB() {
        String updatedTitle = "New title";
        LocalDate updatedReleaseDate = LocalDate.of(2023,9, 28);
        String updateDirector = "New director";
        int updatedDurationMinutes = 100;
        String updatedDescription = "New description";

        MoviesEntity saved = moviesDao.save(getEntity());

        MoviesEntity expectedUpdatedEntity = new MoviesEntity(
                saved.getId(),
                updatedTitle,
                updatedReleaseDate,
                updateDirector,
                updatedDurationMinutes,
                updatedDescription,
                saved.getGenres()
        );

        boolean actualIsUpdated = moviesDao.update(expectedUpdatedEntity);

        assertThat(moviesTable).row(0)
                .value().isEqualTo(expectedUpdatedEntity.getId())
                .value().isEqualTo(expectedUpdatedEntity.getTitle())
                .value().isEqualTo(expectedUpdatedEntity.getReleaseDate())
                .value().isEqualTo(expectedUpdatedEntity.getDirector())
                .value().isEqualTo(expectedUpdatedEntity.getDurationMinutes())
                .value().isEqualTo(expectedUpdatedEntity.getDescription());

        MoviesEntity actualUpdatedEntity = moviesDao.findById(expectedUpdatedEntity.getId());

        Assertions.assertTrue(actualIsUpdated);
        assertThat(actualUpdatedEntity).isEqualTo(expectedUpdatedEntity);
    }

    @Test
    void updateShouldNotUpdateMovieReturnFalseCauseNoIdFoundAndCheckThatSpecifiedEntityIsNotExistInDB() {
        int fakeId = 0;
        MoviesEntity fakeEntity = getEntity();
        fakeEntity.setId(fakeId);

        assertThat(moviesTable).exists();
        assertThat(moviesTable).hasNumberOfRows(0);

        boolean actualIsUpdated = moviesDao.update(fakeEntity);
        MoviesEntity shouldNotExist = moviesDao.findById(fakeId);

        Assertions.assertFalse(actualIsUpdated);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldDeleteMovieReturnTrueAndCheckDeletedEntityInDB() {
        MoviesEntity saved = moviesDao.save(getEntity());

        boolean actualIsDeleted = moviesDao.delete(saved.getId());

        assertThat(moviesTable).hasNumberOfRows(0);

        MoviesEntity shouldNotExist = moviesDao.findById(saved.getId());

        Assertions.assertTrue(actualIsDeleted);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldNotDeleteMovieReturnFalseCauseNoSuchIdFoundAndCheckThatSpecifiedEntityIsNotExistInDB() {
        int fakeId = 0;

        assertThat(moviesTable).exists();
        assertThat(moviesTable).hasNumberOfRows(0);

        boolean actualIsDeleted = moviesDao.delete(fakeId);
        MoviesEntity shouldNotExist = moviesDao.findById(fakeId);

        Assertions.assertFalse(actualIsDeleted);
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