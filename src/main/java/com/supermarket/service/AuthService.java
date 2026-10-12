package com.supermarket.service;

import com.supermarket.dao.UserDAO;
import com.supermarket.model.AppUser;
import com.supermarket.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AppUser login(
            String username,
            String password) throws Exception {

        if (username == null ||
                username.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        if (password == null ||
                password.isEmpty()) {

            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        String passwordHash =
                PasswordUtil.hashPassword(password);

        return userDAO.login(
                username.trim(),
                passwordHash
        );
    }
}