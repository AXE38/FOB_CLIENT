package ru.axe.abs;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AuthWindow {

    @FXML
    private Button btn_ok;
    @FXML
    private ComboBox t_server;

    @FXML
    private TextField t_login;

    @FXML
    private PasswordField t_pass;
    @FXML
    protected void onStart() {
        t_server.getItems().add(Handler.getApi_conn());
        t_server.getSelectionModel().select(0);
    }
    @FXML
    protected void onCancelBtnClick() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    protected void onOKBtnClick(ActionEvent event) {
        var res = Handler.Auth(t_login.getText(), t_pass.getText());
        Alert alert;
        switch (res.getState()) {
            case "200" -> ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
            case "-3" -> {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setContentText("Авторизация");
                alert.setHeaderText("Неправильное имя пользователя или пароль");
                alert.showAndWait();
            }
            case "-4" -> {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setContentText("Авторизация");
                alert.setHeaderText("Сервер недоступен или доступ запрещен");
                alert.showAndWait();
            }
            default -> System.out.println("kek");
        }

    }

    @FXML
    protected void onTPassClick(MouseEvent e) {
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            if (e.getClickCount() == 2) {
                t_pass.setText(t_login.getText());
                var actionEvent = new ActionEvent() {
                    {
                        source = btn_ok;
                    }
                };
                onOKBtnClick(actionEvent);
            }
        }
    }
}
