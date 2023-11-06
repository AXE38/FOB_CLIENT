package ru.axe.abs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Filter {

    private MainWindow owner;

    public void setOwner(MainWindow owner) {
        this.owner = owner;
    }

    @FXML
    private TextField tb_first_name;

    @FXML
    private TextField tb_id;

    @FXML
    private TextField tb_last_name;

    @FXML
    private TextField tb_middle_name;

    @FXML
    private TextField tb_pass;

    @FXML
    private TextField tb_phone;

    @FXML
    protected void onCancelBtnClick(ActionEvent e) {
        ((Stage)(((Button)e.getSource()).getScene().getWindow())).close();
    }

    @FXML
    protected void onOkBtnClick(ActionEvent e) {
        ArrayList<DB_Where> where = new ArrayList<>();
        if (tb_id.getText().length() > 0) {
            where.add(new DB_Where("id", "=", tb_id.getText()));
        }
        if (tb_first_name.getText().length() > 0) {
            where.add(new DB_Where("first_name", "LIKE", tb_first_name.getText() + "%"));
        }
        if (tb_last_name.getText().length() > 0) {
            where.add(new DB_Where("last_name", "LIKE", tb_last_name.getText() + "%"));
        }
        if (tb_middle_name.getText().length() > 0) {
            where.add(new DB_Where("middle_name", "LIKE", tb_middle_name.getText() + "%"));
        }
        if (tb_phone.getText().length() > 0) {
            where.add(new DB_Where("phone", "=", tb_phone.getText() + "%"));
        }
        if (tb_pass.getText().length() > 0) {
            where.add(new DB_Where("pass", "=", tb_pass.getText() + "%"));
        }
        this.owner.setClientFilter(where);
        ((Stage)(((Button)e.getSource()).getScene().getWindow())).close();
    }

    @FXML
    protected void onClearBtnClick(ActionEvent e) {
        this.owner.setClientFilter(null);
        ((Stage)(((Button)e.getSource()).getScene().getWindow())).close();
    }

    public void onStart() {
        var where = owner.getClientFilter();
        if (where == null) return;
        for (var item : where) {
            switch (item.getColumn()) {
                case "id" -> this.tb_id.setText(item.getValue());
                case "first_name" -> this.tb_first_name.setText(item.getValue());
                case "last_name" -> this.tb_last_name.setText(item.getValue());
                case "middle_name" -> this.tb_middle_name.setText(item.getValue());
                case "pass" -> this.tb_pass.setText(item.getValue());
                case "phone" -> this.tb_phone.setText(item.getValue());
            }
        }
    }
}
