package no.kristiania.database;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
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
        Worker worker = new Worker();
        worker.setFirstName(scanner.nextLine());

        workerDao.insert(worker);
        System.out.println(workerDao.list());
    }

    public void insert(Worker worker) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO worker (first_name, last_name, email_address) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                    )) {
                statement.setString(1, worker.getFirstName());
                statement.setString(2, worker.getLastName());
                statement.setString(3, worker.getEmailAddress());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    worker.setId(generatedKeys.getLong("id"));
                }
            }
        }
    }

    public Worker retrieve(Long id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from worker WHERE id = ?")) {
                statement.setLong(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapRowToWorker(rs);
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    private Worker mapRowToWorker(ResultSet rs) throws SQLException {
        Worker worker = new Worker();
        worker.setId(rs.getLong("id"));
        worker.setFirstName(rs.getString("first_name"));
        worker.setLastName(rs.getString("last_name"));
        worker.setEmailAddress(rs.getString("email_address"));
        return worker;
    }

    public List<Worker> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from worker")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<Worker> workers = new ArrayList<>();
                    while (rs.next()) {
                        workers.add(mapRowToWorker(rs));
                    }
                    return workers;
                }
            }
        }
    }

}
