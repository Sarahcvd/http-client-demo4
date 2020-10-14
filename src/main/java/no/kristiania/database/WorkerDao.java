package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WorkerDao {

    private final DataSource dataSource;

    public WorkerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) throws SQLException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/kristianiaworker");
        dataSource.setUser("kristianiashop");
        dataSource.setPassword("harasilaw");
        Flyway.configure().dataSource(dataSource).load().migrate();

        WorkerDao workerDao = new WorkerDao(dataSource);

        System.out.println("Please enter worker name:");
        Scanner scanner = new Scanner(System.in);
        String workerName = scanner.nextLine();

        Worker worker = new Worker();
        worker.setName(workerName);
        workerDao.insert(worker);
        for (Worker _worker : workerDao.list()) {
            System.out.println(_worker);
        }
    }

    public void insert(Worker worker) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO worker (first_name) VALUES (?)")) {
                statement.setString(1, worker.getName());
                statement.executeUpdate();
            }
        }
    }

    public List<Worker> list() throws SQLException {
        List<Worker> workers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from worker")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        Worker worker = new Worker();
                        worker.setName(rs.getString("first_name"));
                        workers.add(worker);
                    }
                }
            }
        }
        return workers;
    }
}
