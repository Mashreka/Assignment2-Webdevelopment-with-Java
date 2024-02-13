package models;

import java.sql.*;

public class DatabaseConnector {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3308/gritAcademy";
    private static final String READ_USER = "user1";
    private static final String READ_PASSWORD = "";
    private static final String INSERT_USER = "user2";
    private static final String INSERT_PASSWORD = "";

    public static Connection connectForRead() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, READ_USER, READ_PASSWORD);
    }

    public static Connection connectForInsert() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, INSERT_USER, INSERT_PASSWORD);
    }
}
