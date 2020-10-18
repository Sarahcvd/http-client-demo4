package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class WorkerDaoTest {
    private WorkerDao workerDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
        workerDao = new WorkerDao(dataSource);
    }

    // (Siste 2 slides forelesning 8)
    // private WorkerDao workerDao = new WorkerDao(createTestDataSource());

    @Test
    void shouldListInsertedWorkers() throws SQLException {
        Worker worker = exampleWorker();
        workerDao.insert(worker);
        assertThat(workerDao.list()).contains(worker.getName());
    }

    @Test
    void shouldRetrieveAllWorkerProperties() throws SQLException {
        Worker worker = exampleWorker();
        workerDao.insert(worker);
        assertThat(workerDao.retrieve(worker.getId()))
                .isEqualTo(worker);
    }

    private Worker exampleWorker() {
        return new Worker();
    }

    // (Siste 2 slides forelesning 8)
    /* @Test
    void shouldRetrieveInsertedWorker() throws SQLException {
        Worker worker = exampleWorker();
        workerDao.insert(worker);
        assertThat(worker).hasNoNullFieldsOrProperties();
        assertThat(workerDao.retrieve(worker.getId()))
                .usingFieldByFieldElementComparator()
                .isEqualTo(worker);
    }
    // (Siste 2 slides forelesning 8)
    private JdbcDataSource createTestDataSource(){
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    } */

    /* private Worker exampleWorker(){
        Worker worker = new Worker();
        worker.setName(exampleWorkerName());
        return worker;
    } */

    /** Returns a random worker name */
    private String exampleWorkerName() {
        String[] options = {"Johannes", "Christian", "Lucas", "Matheus", "Markus"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}