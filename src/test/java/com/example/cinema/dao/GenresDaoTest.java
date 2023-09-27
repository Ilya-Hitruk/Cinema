package com.example.cinema.dao;

import com.example.cinema.model.entity.GenresEntity;
import com.example.cinema.util.connection.pool.ConnectionManager;
import lombok.NonNull;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;

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
    static Source dataSource;
    static Table genresTable;

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

        dataSource = new Source(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );

        genresTable = new Table(dataSource, "genres");
    }

    @AfterEach
    void tearDown() {
        postgres.stop();
    }

    @Test
    void saveShouldSaveGenreAndCheckCreatedEntityInDB() {
        GenresEntity expectedAdventureEntity = getAdventureEntity();
        GenresEntity expectedFantasyEntity = getFantasyEntity();

        assertThat(genresTable).exists();

        GenresEntity adventureActual = genresDao.save(expectedAdventureEntity);
        GenresEntity fantasyActual = genresDao.save(expectedFantasyEntity);

        assertThat(genresTable).hasNumberOfRows(2);

        assertThat(genresTable).column("genre")
                .value().isEqualTo(expectedAdventureEntity.getTitle())
                .value().isEqualTo(expectedFantasyEntity.getTitle());

        assertThat(genresTable).row(0)
                .value().isEqualTo(adventureActual.getId())
                .value().isEqualTo(expectedAdventureEntity.getTitle());

        assertThat(genresTable).row(1)
                .value().isEqualTo(fantasyActual.getId())
                .value().isEqualTo(expectedFantasyEntity.getTitle());

        assertThat(adventureActual).isEqualTo(expectedAdventureEntity);
        assertThat(fantasyActual).isEqualTo(expectedFantasyEntity);
    }

//    @Test
//    void findMoviesByGenre() {
//
//    }

    @Test
    void findAllShouldReturnAllExistingGenresAndCheckExistingEntitiesInDB() {
        List<GenresEntity> expectedListOfGenres = getListOfGenres();

        List<GenresEntity> savedList = genresDao.save(expectedListOfGenres);

        assertThat(genresTable).hasNumberOfRows(expectedListOfGenres.size());

        for (GenresEntity savedEntity: savedList) {
            String expectedTitle = expectedListOfGenres
                    .get(savedEntity.getId() - 1)
                    .getTitle();

            assertThat(genresTable).row(savedEntity.getId() - 1)
                    .value().isEqualTo(savedEntity.getId())
                    .value().isEqualTo(expectedTitle);
        }

        List<GenresEntity> actualList = genresDao.findAll();

        assertThat(actualList).hasSize(expectedListOfGenres.size());
        assertThat(actualList).containsAll(expectedListOfGenres);
    }

    @Test
    void findByIdShouldReturnGenreAndCheckExistingEntityInDB() {
        GenresEntity expectedEntity = getAdventureEntity();

        int idOfSavedEntity = genresDao.save(expectedEntity).getId();

        assertThat(genresTable).row(0)
                .value().isEqualTo(idOfSavedEntity)
                .value().isEqualTo(expectedEntity.getTitle());

        GenresEntity actual = genresDao.findById(idOfSavedEntity);

        assertThat(actual).isEqualTo(expectedEntity);
    }

    @Test
    void findByGenreTitleShouldReturnGenreAndCheckExistingEntityInDB() {
        GenresEntity expectedEntity = getAdventureEntity();

        int idOfSavedEntity = genresDao.save(expectedEntity).getId();

        assertThat(genresTable).row(0)
                .value().isEqualTo(idOfSavedEntity)
                .value().isEqualTo(expectedEntity.getTitle());

        GenresEntity actual = genresDao.findByGenreTitle(expectedEntity.getTitle());

        assertThat(actual).isEqualTo(expectedEntity);
    }

    @Test
    void findByIdShouldReturnNullCauseNoSuchIdFoundAndCheckThatSpecifiedEntityIsNotExistInDB() {
        int fakeId = 0;

        assertThat(genresTable).exists();
        assertThat(genresTable).hasNumberOfRows(0);

        GenresEntity actual = genresDao.findById(fakeId);

        assertThat(actual).isNull();
    }

    @Test
    void findByGenreTitleShouldReturnNullCauseNoSuchTitleFoundAndCheckThatSpecifiedEntityIsNotExistInDB() {
        String fakeTitle = "fake";

        assertThat(genresTable).exists();
        assertThat(genresTable).hasNumberOfRows(0);

        GenresEntity actual = genresDao.findByGenreTitle(fakeTitle);

        assertThat(actual).isNull();
    }

    @Test
    void updateShouldUpdateGenreReturnTrueAndCheckUpdatedEntityInDB() {
        String updatedTitle = "Fantasy";

        GenresEntity saved = genresDao.save(getAdventureEntity());

        GenresEntity expectedUpdatedEntity = new GenresEntity(saved.getId(), updatedTitle);

        boolean actualIsUpdated = genresDao.update(expectedUpdatedEntity);

        assertThat(genresTable).row(0)
                .value().isEqualTo(saved.getId())
                .value().isEqualTo(updatedTitle);

        String actualTitle = genresDao.findById(expectedUpdatedEntity.getId()).getTitle();

        Assertions.assertTrue(actualIsUpdated);
        assertThat(actualTitle).isEqualTo(updatedTitle);
    }

    @Test
    void updateShouldNotUpdateGenreReturnFalseCauseNoSuchIdFoundAndCheckThatSpecifiedEntityIsNotExistInDB() {
        GenresEntity fakeEntity = new GenresEntity(0, "fake");

        assertThat(genresTable).exists();
        assertThat(genresTable).hasNumberOfRows(0);

        boolean actualIsUpdated = genresDao.update(fakeEntity);
        GenresEntity shouldNotExist = genresDao.findById(fakeEntity.getId());

        Assertions.assertFalse(actualIsUpdated);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldDeleteGenreReturnTrueAndCheckDeletedEntityInDB() {
        GenresEntity saved = genresDao.save(getAdventureEntity());

        boolean actualIsDeleted = genresDao.delete(saved.getId());

        assertThat(genresTable).hasNumberOfRows(0);

        GenresEntity shouldNotExist = genresDao.findById(saved.getId());

        Assertions.assertTrue(actualIsDeleted);
        assertThat(shouldNotExist).isNull();
    }

    @Test
    void deleteShouldReturnFalseCauseNoSuchIdFoundAndCheckThatSpecifiedEntityIsNotExistInDB() {
        int fakeId = 0;

        assertThat(genresTable).exists();
        assertThat(genresTable).hasNumberOfRows(0);

        boolean actual = genresDao.delete(fakeId);
        GenresEntity shouldNotExist = genresDao.findById(fakeId);

        Assertions.assertFalse(actual);
        assertThat(shouldNotExist).isNull();
    }

    @NonNull
    private static GenresEntity getAdventureEntity() {
        return new GenresEntity("Adventure");
    }

    @NonNull
    private static GenresEntity getFantasyEntity() {
        return new GenresEntity("Fantasy");
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