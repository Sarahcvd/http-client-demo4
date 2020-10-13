package no.kristiania.database;

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

        WorkerDao workerDao = new WorkerDao(dataSource);

        System.out.println("Please enter worker name:");
        Scanner scanner = new Scanner(System.in);
        String workerName = scanner.nextLine();

        workerDao.insert(workerName);
        for (String worker : workerDao.list()) {
            System.out.println(worker);
        }
    }



    public void insert(String worker) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO worker (full_name) VALUES (?)")) {
                statement.setString(1, worker);
                statement.executeUpdate();
            }
        }
    }

    public List<String> list() throws SQLException {
        List<String> workers = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from worker")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        workers.add(rs.getString("full_name"));
                    }
                }
            }
        }
        return workers;
    }
}
