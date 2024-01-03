package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        connection = DriverManager.getConnection(url);
        connection.createStatement().execute("create table Users (" +
                "uuid varchar(200) primary key, " +
                "email varchar(200))");
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Order.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws ExecutionException, InterruptedException, SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking new user");
        System.out.println(record.key());
        System.out.println(record.value());
        Order order = record.value();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order);
        }
    }

    private void insertNewUser(Order order) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into Users (uuid, email) " +
                " values (?,?)");
        preparedStatement.setString(1, order.getUserId());
        preparedStatement.setString(2, order.getEmail());
        preparedStatement.execute();
        System.out.println("Usuario: " + order.getUserId());
    }

    private boolean isNewUser(String email) throws SQLException {
        PreparedStatement exists = connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        exists.setString(1, email);
        ResultSet results = exists.executeQuery();
        return !results.next();
    }
}
