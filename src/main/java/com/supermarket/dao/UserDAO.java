package com.supermarket.dao;

import com.supermarket.config.DBConnection;
import com.supermarket.enums.Role;
import com.supermarket.model.AppUser;
import com.supermarket.util.SQLQueryLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    private static final String SQL_FILE =
            "sql/04_crud_queries/user_queries.sql";

    public AppUser login(
            String username,
            String passwordHash) throws Exception {

        String sql = SQLQueryLoader.load(
                SQL_FILE,
                "LOGIN_USER"
        );

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);
            statement.setString(2, passwordHash);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    AppUser user = new AppUser();

                    user.setUserId(
                            resultSet.getInt("user_id")
                    );

                    user.setUsername(
                            resultSet.getString("username")
                    );

                    user.setPasswordHash(
                            resultSet.getString("password_hash")
                    );

                    user.setFullName(
                            resultSet.getString("full_name")
                    );

                    user.setRole(
                            Role.valueOf(
                                    resultSet.getString("role")
                            )
                    );

                    user.setActive(
                            resultSet.getBoolean("active")
                    );

                    return user;
                }
            }
        }

        return null;
    }
}