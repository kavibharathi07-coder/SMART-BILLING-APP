package com.supermarket.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {

    private static final Properties PROPERTIES = loadProperties();

    private DBConnection() {
    }

    public static Connection getConnection()
            throws SQLException {
        return DriverManager.getConnection(PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.username"), PROPERTIES.getProperty("db.password"));
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("db.properties was not found on the classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read database configuration", e);
        }
        return properties;
    }
}
