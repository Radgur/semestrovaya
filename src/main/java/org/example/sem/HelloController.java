package org.example.sem;

import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;

public class HelloController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passField;

    @FXML
    private Button regButton;

    @FXML
    private Button signInButton;

    @FXML
    void initialize() {
        regButton.setOnAction(event->{
           regButton.getScene().getWindow().hide();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("registration.fxml"));
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
        signInButton.setOnAction(event -> {
            String username = loginField.getText().trim();
            String password = passField.getText().trim();

            // Проверка, что поля не пустые
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Ошибка авторизации", "Все поля должны быть заполнены!");
                return;
            }

            try {
                DbHandler con = DbHandler.getInstance();
                boolean success = con.loginUser(username, password);

                if (success) {
                    // Успешный вход, переход в окно чата
                    showAlert(Alert.AlertType.INFORMATION, "Успешный вход", "Добро пожаловать, " + username + "!");
                    signInButton.getScene().getWindow().hide();
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("chat.fxml"));
                    Stage stage = new Stage();
                    stage.setResizable(false);
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setTitle("Semestrovka - Чат");
                    stage.setScene(scene);
                    stage.show();
                } else {
                    // Ошибка авторизации: неверный логин или пароль
                    showAlert(Alert.AlertType.ERROR, "Ошибка авторизации", "Неверный логин или пароль!");
                }
            } catch (SQLException e) {
                // Ошибка подключения к базе данных
                showAlert(Alert.AlertType.ERROR, "Ошибка базы данных", "Ошибка при подключении к базе данных.");
                e.printStackTrace();
            } catch (IOException e) {
                // Ошибка загрузки интерфейса
                showAlert(Alert.AlertType.ERROR, "Ошибка интерфейса", "Ошибка загрузки окна чата.");
                e.printStackTrace();
            }
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
