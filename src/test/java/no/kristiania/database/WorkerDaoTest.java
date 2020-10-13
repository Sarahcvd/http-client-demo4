package no.kristiania.database;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class WorkerDaoTest {
    @Test
    void shouldListInsertedWorkers() {
        WorkerDao workerDao = new WorkerDao();
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