package ru.axe.abs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.axe.abs.models.DBClient;

import java.util.Objects;

public class AddClient {
    public Button cancelBtn;
    private DBClient edited;
    private DBClient v_client;

    @FXML
    private TextField tb_last_name;

    @FXML
    private TextField tb_first_name;

    @FXML
    private TextField tb_middle_name;

    @FXML
    private DatePicker dp_date_of_birth;

    @FXML
    private TextField tb_pass;

    @FXML
    private TextField tb_phone;

    public void setDBClient(DBClient client) {
        this.edited = client;
    }

    @FXML
    protected void onStart() {
        if (edited != null) {
            v_client = new DBClient(edited);
            this.tb_first_name.setText(edited.getFirst_name());
            this.tb_last_name.setText(edited.getLast_name());
            this.tb_middle_name.setText(edited.getMiddle_name());
            this.dp_date_of_birth.setValue(edited.getDate_of_birth());
            this.tb_pass.setText(edited.getPass());
            this.tb_phone.setText(edited.getPhone());
        } else {
            v_client = new DBClient();
        }
    }

    @FXML
    protected void onCancelBtnClick(ActionEvent e) {
        ((Stage)(((Button)e.getSource()).getScene().getWindow())).close();
    }

    @FXML
    protected void onOkBtnClick(ActionEvent e) {
        if (
                   this.dp_date_of_birth.getValue() == null
                || Objects.equals(this.tb_pass.getText(), "")
                || Objects.equals(this.tb_phone.getText(), "")
                || Objects.equals(this.tb_first_name.getText(), "")
                || Objects.equals(this.tb_last_name.getText(), "")
        ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setContentText("Редактирование");
            alert.setHeaderText("Не все обязательные поля заполнены");
            alert.showAndWait();
            return;
        }
        v_client.setDate_of_birth(this.dp_date_of_birth.getValue());
        v_client.setFirst_name(this.tb_first_name.getText());
        v_client.setLast_name(this.tb_last_name.getText());
        v_client.setMiddle_name(this.tb_middle_name.getText());
        v_client.setPass(this.tb_pass.getText());
        v_client.setPhone(this.tb_phone.getText());
        if (edited != null) {
            if (v_client.equals(edited)) {
                ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
            } else {
                DB_Result res = Handler.editItems("CLIENT", v_client);
                if (!res.getState().equals("200")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!");
                    alert.setContentText("Изменение");
                    alert.setHeaderText(res.getState() + ": " + res.getMsg());
                    alert.showAndWait();
                }
            }
        } else {
            DB_Result res = Handler.addItems("CLIENT", v_client);
            if (!res.getState().equals("200")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setContentText("Добавление");
                alert.setHeaderText(res.getState() + ": " + res.getMsg());
                alert.showAndWait();
            }
            ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
        }
    }
}
