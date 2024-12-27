package org.example.sem;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
public class ChatController {
    @FXML
    private ListView<String> messageList;  // Список сообщений

    @FXML
    private ListView<String> messageListView;  // Список участников

    @FXML
    private TextField messageInput;  // Поле для ввода сообщения

    @FXML
    private Button sendButton;  // Кнопка отправки сообщения

    @FXML
    void initialize() {
        // Обработчик кнопки отправки
        sendButton.setOnAction(event -> sendMessage());

        // Обработка нажатия Enter
        messageInput.setOnAction(event -> sendMessage());
    }
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            messageList.getItems().add("Вы: " + message);  // Добавляем сообщение в список
            messageInput.clear();  // Очищаем поле ввода
        }
    }
}