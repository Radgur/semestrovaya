package org.example.sem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;

public class DbHandler {

    private static final Logger logger = LoggerFactory.getLogger(DbHandler.class);

    private static final String CON_STR = "jdbc:sqlite:users.db";
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }

    private Connection connection;

    private DbHandler() throws SQLException {
        DriverManager.registerDriver(new JDBC());
        this.connection = DriverManager.getConnection(CON_STR);
        logger.info("Database connected successfully.");
    }

    public void CreateUser(String username, String pswd) {
        try {
            String salt = generateSalt();
            String hashedPassword = hashPassword(pswd, salt);

            try (PreparedStatement statement = this.connection.prepareStatement(
                    "INSERT INTO Users(`name`, `pswd`, `salt`) VALUES(?, ?, ?)")) {
                statement.setObject(1, username);
                statement.setObject(2, hashedPassword);
                statement.setObject(3, salt);
                statement.execute();
                logger.info("User '{}' created successfully.", username);
            }

        } catch (SQLException e) {
            logger.error("Error while adding user to the database", e);
            throw new RuntimeException("Ошибка при добавлении пользователя в базу данных", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing password", e);
            throw new RuntimeException("Ошибка хэширования пароля", e);
        }
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String saltedPassword = password + salt;
        byte[] hashedBytes = md.digest(saltedPassword.getBytes());
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    public boolean loginUser(String username, String pswd) {
        try {
            try (PreparedStatement statement = this.connection.prepareStatement(
                    "SELECT pswd, salt FROM Users WHERE name = ?")) {
                statement.setObject(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedHashedPassword = resultSet.getString("pswd");
                        String storedSalt = resultSet.getString("salt");

                        String hashedPassword = hashPassword(pswd, storedSalt);

                        boolean valid = storedHashedPassword.equals(hashedPassword);
                        if (valid) {
                            logger.info("User '{}' logged in successfully.", username);
                        } else {
                            logger.warn("Failed login attempt for user '{}'.", username);
                        }
                        return valid;
                    } else {
                        logger.warn("User '{}' not found.", username);
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking user credentials", e);
            throw new RuntimeException("Ошибка при проверке пользователя", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error hashing password", e);
            throw new RuntimeException("Ошибка хэширования пароля", e);
        }
    }
}