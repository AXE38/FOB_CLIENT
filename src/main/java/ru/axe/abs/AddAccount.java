package ru.axe.abs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.w3c.dom.NodeList;
import ru.axe.abs.models.DBAccount;
import ru.axe.abs.models.DBDict;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class AddAccount {

    private DBAccount edited;
    private DBAccount v_account;
    @FXML
    private TextField tb_client;
    @FXML
    private TextField tb_num;
    @FXML
    private TextField tb_agreement_id;
    @FXML
    private ComboBox cb_loan_type;
    @FXML
    private TextField tb_card_pan;
    @FXML
    private TextField tb_iss_s;
    @FXML
    private TextField tb_s;
    @FXML
    private DatePicker dp_open_date;
    @FXML
    private DatePicker dp_plan_close_date;
    @FXML
    private TextField tb_interest_rate;

    public void setDBAccount(DBAccount acc) {
        this.edited = acc;
    }
    @FXML
    protected void onStart() {
        ObservableList<DBDict> cb_items = FXCollections.observableArrayList();
        cb_items.addAll(Handler.getDictGroup("LOAN_TYPE"));
        this.cb_loan_type.setItems(cb_items);
        this.cb_loan_type.getSelectionModel().selectFirst();
        if (edited == null) {
            v_account = new DBAccount();
        } else {
            v_account = new DBAccount(edited);
            setClient(edited.getCollection_id());
            this.tb_agreement_id.setText(edited.getAgreement_id());
            this.tb_card_pan.setText(edited.getCard_pan());
            this.tb_interest_rate.setText(String.valueOf(edited.getInterest_rate()));
            this.tb_num.setText(edited.getNum());
            this.tb_iss_s.setText(String.valueOf(edited.getIss_s()));
            this.tb_s.setText(String.valueOf(edited.getS()));
            this.dp_open_date.setValue(edited.getOpen_date());
            this.dp_plan_close_date.setValue(edited.getPlan_close_date());

        }
    }

    protected void setClient(long id) {
        this.v_account.setCollection_id(id);

        ArrayList<DB_Where> where = new ArrayList<>() {
            {
                add(new DB_Where("id", "=", String.valueOf(id)));
            }
        };
        var res = (NodeList)Handler.selectItems("CLIENT", new String[]{"first_name", "last_name", "middle_name"}, where, Optional.empty(), Optional.empty()).getResult();
        String fullname = res.item(0).getChildNodes().item(0).getTextContent() + " "
                + res.item(0).getChildNodes().item(1).getTextContent() + " "
                + res.item(0).getChildNodes().item(2).getTextContent();
        tb_client.setText(fullname);
    }
    @FXML
    protected void onCancelBtnClick(ActionEvent e) {
        ((Stage)(((Button)e.getSource()).getScene().getWindow())).close();
    }
    @FXML
    protected void onOkBtnClick(ActionEvent e) {
        if (
                this.dp_open_date.getValue() == null
                        || Objects.equals(this.tb_agreement_id.getText(), "")
                        || Objects.equals(this.tb_iss_s.getText(), "")
                        || Objects.equals(this.tb_interest_rate.getText(), "")
                        || Objects.equals(this.tb_s.getText(), "")
        ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setContentText("Редактирование");
            alert.setHeaderText("Не все обязательные поля заполнены");
            alert.showAndWait();
            return;
        }
        v_account.setOpen_date(this.dp_open_date.getValue());
        v_account.setPlan_close_date(this.dp_plan_close_date.getValue());
        v_account.setNum(this.tb_num.getText());
        v_account.setIss_s(Float.parseFloat(this.tb_iss_s.getText()));
        v_account.setAgreement_id(this.tb_agreement_id.getText());
        v_account.setInterest_rate(Float.parseFloat(this.tb_interest_rate.getText()));
        v_account.setCard_pan(this.tb_card_pan.getText());
        v_account.setLoan_type(((DBDict)cb_loan_type.getSelectionModel().getSelectedItem()).getId());
        if (edited != null) {
            if (v_account.equals(edited)) {
                ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
            } else {
                DB_Result res = Handler.editItems("ACCOUNT", v_account);
                if (!res.getState().equals("200")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка!");
                    alert.setContentText("Изменение");
                    alert.setHeaderText(res.getState() + ": " + res.getMsg());
                    alert.showAndWait();
                }
                ((Stage) (((Button) e.getSource()).getScene().getWindow())).close();
            }
        } else {
            DB_Result res = Handler.addItems("ACCOUNT", v_account);
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
