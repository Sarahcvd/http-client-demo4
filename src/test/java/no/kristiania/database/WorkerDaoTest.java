package no.kristiania.database;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class WorkerDaoTest {
    @Test
    void shouldListInsertedWorkers() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:testdatabase;DB_CLOSE_DELAY=-1");
        try (Connection connection = dataSource.getConnection()){
            connection.prepareStatement("create table worker (full_name varchar)").executeUpdate();
        }

        WorkerDao workerDao = new WorkerDao(dataSource);
        String worker = exampleWorker();
        workerDao.insert(worker);
        assertThat(workerDao.list()).contains(worker);
    }

    /** Returns a random worker name */
    private String exampleWorker() {
        String[] options = {"Johannes", "Christian", "Lucas", "Matheus", "Markus"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }
}