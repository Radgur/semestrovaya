package org.example.sem;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class RegController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField loginField;

    @FXML
    private Button newUserButton, signInButton;
    @FXML
    private PasswordField passField;

    @FXML
    private Label statusLabel = new Label();

    @FXML
    void initialize() {
        newUserButton.setOnAction(event -> {
            String username = loginField.getText().trim();
            String password = passField.getText().trim();

            // Проверка, что поля не пустые
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Ошибка регистрации", "Все поля должны быть заполнены!");
                return;
            }

            try {
                DbHandler con = DbHandler.getInstance();
                try {
                    // Попытка создать нового пользователя
                    con.CreateUser(username, password);
                    showAlert(Alert.AlertType.INFORMATION, "Успешная регистрация", "Пользователь успешно зарегистрирован!");

                    // Переход на экран авторизации
                    newUserButton.getScene().getWindow().hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
                    Stage stage = new Stage();
                    stage.setResizable(false);
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setTitle("Semestrovka");
                    stage.setScene(scene);
                    stage.show();
                } catch (RuntimeException e) {
                    if (e.getMessage().contains("UNIQUE constraint failed")) {
                        // Ошибка: пользователь уже существует
                        showAlert(Alert.AlertType.ERROR, "Ошибка регистрации", "Пользователь с таким именем уже существует!");
                    } else {
                        // Другая ошибка при добавлении пользователя
                        showAlert(Alert.AlertType.ERROR, "Ошибка регистрации", "Произошла ошибка при регистрации пользователя.");
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                // Ошибка подключения к базе данных
                showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Не удалось подключиться к базе данных.");
                e.printStackTrace();
            } catch (IOException e) {
                // Ошибка загрузки интерфейса
                showAlert(Alert.AlertType.ERROR, "Ошибка интерфейса", "Ошибка загрузки формы авторизации.");
                e.printStackTrace();
            }
        });

        signInButton.setOnAction(event-> {
            signInButton.getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Stage stage = new Stage();
            stage.setResizable(false);
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setTitle("Semestrovka");
            stage.setScene(scene);
            stage.show();
            });
    }
    // Вспомогательный метод для отображения Alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}