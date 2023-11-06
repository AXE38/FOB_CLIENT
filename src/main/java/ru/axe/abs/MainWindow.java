package ru.axe.abs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.w3c.dom.NodeList;
import ru.axe.abs.models.DBAccount;
import ru.axe.abs.models.DBClient;
import ru.axe.abs.models.DBDict;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class MainWindow {


    public ArrayList<DB_Where> getClientFilter() {
        return clientFilter;
    }

    public void setClientFilter(ArrayList<DB_Where> clientFilter) {
        this.clientFilter = clientFilter;
    }

    private ArrayList<DB_Where> clientFilter = null;
    private final ArrayList<ColumnAlias> aliases = new ArrayList<>() {
        {
            add(new ColumnAlias(1, "id", "ID", "CLIENT"));
            add(new ColumnAlias(2, "last_name", "Фамилия", "CLIENT"));
            add(new ColumnAlias(3, "first_name", "Имя", "CLIENT"));
            add(new ColumnAlias(4, "middle_name", "Отчество", "CLIENT"));
            add(new ColumnAlias(5, "date_of_birth", "Дата рождения", "CLIENT"));
            add(new ColumnAlias(6, "pass", "Паспорт", "CLIENT"));
            add(new ColumnAlias(7, "phone", "Номер телефона", "CLIENT"));
            add(new ColumnAlias(8, "create_date", "Дата создания", "CLIENT"));
            add(new ColumnAlias(9, "create_user", "Создавший пользователь", "CLIENT"));
            add(new ColumnAlias(10, "last_update_date", "Дата изменения", "CLIENT"));
            add(new ColumnAlias(11, "last_update_user", "Изменивший пользователь", "CLIENT"));

            add(new ColumnAlias(1, "id", "ID", "ACCOUNT"));
            add(new ColumnAlias(2, "collection_id", "ИД клиента", "ACCOUNT"));
            add(new ColumnAlias(3, "num", "Номер ссудного счёта", "ACCOUNT"));
            add(new ColumnAlias(4, "agreement_id", "Номер договора", "ACCOUNT"));
            add(new ColumnAlias(5, "card_pan", "Номер карты", "ACCOUNT"));
            add(new ColumnAlias(6, "loan_type", "Вид кредита", "ACCOUNT"));
            add(new ColumnAlias(7, "iss_s", "Сумма выдачи", "ACCOUNT"));
            add(new ColumnAlias(8, "s", "Остаток", "ACCOUNT"));
            add(new ColumnAlias(11, "close_date", "Дата закрытия", "ACCOUNT"));
            add(new ColumnAlias(9, "open_date", "Дата открытия", "ACCOUNT"));
            add(new ColumnAlias(10, "plan_close_date", "Плановая дата закрытия", "ACCOUNT"));
            add(new ColumnAlias(12, "interest_rate", "Процентная ставка", "ACCOUNT"));

            add(new ColumnAlias(13, "create_date", "Дата создания", "ACCOUNT"));
            add(new ColumnAlias(14, "create_user", "Создавший пользователь", "ACCOUNT"));
            add(new ColumnAlias(15, "last_update_date", "Дата изменения", "ACCOUNT"));
            add(new ColumnAlias(16, "last_update_user", "Изменивший пользователь", "ACCOUNT"));        }
    };

    @FXML
    private TableView tb_client;

    @FXML
    private TableView tb_account;

    @FXML
    private Menu menuOpers;

    @FXML
    protected void onEditClick() {
        Parent root;
        if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Клиенты")) {
            var cl = (DBClient) tb_client.getSelectionModel().getSelectedItem();

            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                root = loader.load(AuthWindow.class.getResourceAsStream("AddClient.fxml"));
                final AddClient controller = loader.getController();
                controller.setDBClient(cl);
                stage.addEventHandler(WindowEvent.WINDOW_SHOWING, a -> controller.onStart());
                Scene scene = new Scene(root);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("Изменить");
                stage.setScene(scene);
                stage.showAndWait();
                updateTables("CLIENT");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (tabPane.getSelectionModel().getSelectedItem().getText().equals("Кредиты")) {
            var acc = (DBAccount) tb_account.getSelectionModel().getSelectedItem();

            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader();
                root = loader.load(AuthWindow.class.getResourceAsStream("AddAccount.fxml"));
                final AddAccount controller = loader.getController();
                controller.setDBAccount(acc);
                stage.addEventHandler(WindowEvent.WINDOW_SHOWING, a -> {
                    controller.onStart();
                    controller.setClient(acc.getCollection_id());
                });
                Scene scene = new Scene(root);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setTitle("Изменить");
                stage.setScene(scene);
                stage.showAndWait();
                updateTables("ACCOUNT");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    protected void onTblClick(MouseEvent e) {
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            if (e.getClickCount() == 2) {
                onEditClick();
            }
        }
    }

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tabClient, tabAccount, tabAccountPlan, tabAbsUser, tabEtlAccount, tabEtlClient, tabEtlAccountPlan;

    private final ObservableList<DBClient> clients = FXCollections.observableArrayList();
    private final ObservableList<DBAccount> accounts = FXCollections.observableArrayList();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Stage stage;
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.nnnnnn");
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter columnFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public MainWindow() {

    }

    private void populateColumns(Class class_id, TableView table) {
        for (var alias : aliases) {
            try {
                if (!alias.getEntity_type().equals(class_id.getField("entity_type").get(null))) continue;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            for (Method method : class_id.getMethods()) {
                String name = method.getName();
                if (name.startsWith("get") && !name.equals("getClass")) {
                    String propName = name.replace("get", "");
                    if (!propName.toLowerCase().equals(alias.getColumn_name())) continue;

                    try {
                        TableColumn column = new TableColumn(alias.getColumn_alias());
                        if (method.getReturnType().equals(LocalDateTime.class)) {
                            column.setCellFactory(col -> {
                                TableCell<DBClient, LocalDateTime> cell = new TableCell<>() {
                                    @Override
                                    protected void updateItem(LocalDateTime item, boolean empty) {
                                        super.updateItem(item, empty);
                                        if (empty) {
                                            setText(null);
                                        } else {
                                            if (item != null) {
                                                this.setText(item.format(columnFormatter));
                                            }
                                        }
                                    }
                                };
                                return cell;
                            });
                            column.setCellValueFactory(new PropertyValueFactory<DBClient, LocalDateTime>(propName));
                        } else if (method.getReturnType().equals(LocalDate.class)) {
                            if (method.getReturnType().equals(LocalDate.class)) {
                                column.setCellFactory(col -> {
                                    return new TableCell<DBClient, LocalDate>() {
                                        @Override
                                        protected void updateItem(LocalDate item, boolean empty) {
                                            super.updateItem(item, empty);
                                            if (empty) {
                                                setText(null);
                                            } else {
                                                if (item != null) {
                                                    this.setText(item.format(columnFormatter));
                                                }
                                            }
                                        }
                                    };
                                });
                                column.setCellValueFactory(new PropertyValueFactory<DBClient, LocalDate>(propName));
                            }
                        } else {
                            column.setCellValueFactory(new PropertyValueFactory<>(propName));
                        }
                        table.getColumns().add(column);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    @FXML
    protected void onStart() {

        Parent root;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            root = loader.load(AuthWindow.class.getResourceAsStream("AuthWindow.fxml"));
            final AuthWindow controller = loader.getController();
            stage.addEventHandler(WindowEvent.WINDOW_SHOWING, a -> controller.onStart());
            Scene scene = new Scene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Авторизация");
            stage.setScene(scene);
            stage.showAndWait();
            populateColumns(DBClient.class, tb_client);
            populateColumns(DBAccount.class, tb_account);
            tb_client.setItems(this.clients);
            tb_account.setItems(this.accounts);
            recalcVisible();
            updateTables(null);
            this.tb_client.getSelectionModel().select(0);
            this.tb_account.getSelectionModel().select(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recalcVisible() {
        DBDict role = Handler.getRole();
        this.stage.setTitle(this.stage.getTitle() + " Роль: " + role.getDescription());
        switch (role.getCode()) {
            case "USER" -> {
                tabPane.getTabs().remove(tabAccountPlan);
                tabPane.getTabs().remove(tabEtlAccount);
                tabPane.getTabs().remove(tabEtlClient);
                tabPane.getTabs().remove(tabEtlAccountPlan);
                tabPane.getTabs().remove(tabAbsUser);
            }
            case "ADMIN" -> {
                tabPane.getTabs().remove(tabAccountPlan);
                tabPane.getTabs().remove(tabEtlAccount);
                tabPane.getTabs().remove(tabEtlClient);
                tabPane.getTabs().remove(tabEtlAccountPlan);
            }
            case "AUDIT" -> {
                tabPane.getTabs().remove(tabAccountPlan);
                tabPane.getTabs().remove(tabAbsUser);
                tabPane.getTabs().remove(tabClient);
                tabPane.getTabs().remove(tabAccount);
                menuOpers.setVisible(false);
            }
        }
    }

    @FXML
    protected void onDeleteClick() {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление");
        alert.setContentText("Удаление экземпляра 13443");
        alert.setHeaderText("Вы уверены?");

        alert.show();
    }

    @FXML
    protected void onAboutClick() {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setContentText("2022. Все права защищены");
        alert.setHeaderText("АБС Фронт-офис версия 05.2022");
        alert.showAndWait();
    }

    @FXML
    protected void onAddClick() {
        Parent root;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            root = loader.load(AuthWindow.class.getResourceAsStream("AddClient.fxml"));
            final AddClient controller = loader.getController();
            stage.addEventHandler(WindowEvent.WINDOW_SHOWING, a -> controller.onStart());
            Scene scene = new Scene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Добавить");
            stage.setScene(scene);
            stage.showAndWait();
            updateTables("CLIENT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onOpenAccClick() {
        var cl = (DBClient)tb_client.getSelectionModel().getSelectedItem();
        Parent root;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            root = loader.load(AuthWindow.class.getResourceAsStream("AddAccount.fxml"));
            final AddAccount controller = loader.getController();
            stage.addEventHandler(WindowEvent.WINDOW_SHOWING, a -> {
                controller.onStart();
                controller.setClient(cl.getId());
            });
            Scene scene = new Scene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Добавить");
            stage.setScene(scene);
            stage.showAndWait();
            updateTables("ACCOUNT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onFilterClick() {
        Parent root;
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            root = loader.load(AuthWindow.class.getResourceAsStream("Filter.fxml"));
            final Filter controller = loader.getController();
            stage.addEventHandler(WindowEvent.WINDOW_SHOWING, a -> {
                controller.setOwner(this);
                controller.onStart();
            });
            Scene scene = new Scene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setTitle("Фильтр: клиенты");
            stage.setScene(scene);
            stage.showAndWait();
            updateTables("CLIENT");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTables(String entity_type) {
        boolean updateAll = entity_type == null;
        if (entity_type != null && entity_type.equals("CLIENT") || updateAll) {
            this.clients.clear();
            String[] columns = {"id", "last_name", "first_name", "middle_name", "pass", "last_update_date", "last_update_user", "create_date", "create_user", "phone", "date_of_birth"};
            DB_Result res = Handler.selectItems("CLIENT", columns, this.clientFilter, Optional.of(0), Optional.of(200));
            if (res.getState().equals("200")) {
                NodeList clients = (NodeList) res.getResult();
                DBClient client;
                for (int i = 0; i < clients.getLength(); i++) {
                    client = new DBClient();
                    NodeList row = clients.item(i).getChildNodes();
                    for (int j = 0; j < row.getLength(); j++) {
                        if (row.item(j).getTextContent().length() == 0) continue;
                        switch (row.item(j).getNodeName()) {
                            case "id" -> client.setId(Long.parseLong(row.item(j).getTextContent()));
                            case "last_name" -> client.setLast_name(row.item(j).getTextContent());
                            case "first_name" -> client.setFirst_name(row.item(j).getTextContent());
                            case "middle_name" -> client.setMiddle_name(row.item(j).getTextContent());
                            case "date_of_birth" ->
                                    client.setDate_of_birth(LocalDate.parse(row.item(j).getTextContent(), dateFormatter));
                            case "pass" -> client.setPass(row.item(j).getTextContent());
                            case "phone" -> client.setPhone(row.item(j).getTextContent());
                            case "create_date" ->
                                    client.setCreate_date(LocalDateTime.parse(row.item(j).getTextContent(), dateTimeFormatter));
                            case "create_user" -> client.setCreate_user(Long.parseLong(row.item(j).getTextContent()));
                            case "last_update_date" ->
                                    client.setLast_update_date(LocalDateTime.parse(row.item(j).getTextContent(), dateTimeFormatter));
                            case "last_update_user" ->
                                    client.setLast_update_user(Long.parseLong(row.item(j).getTextContent()));
                        }
                    }
                    this.clients.add(client);
                }
            }
        }
        if (entity_type != null && entity_type.equals("ACCOUNT") || updateAll) {
            this.accounts.clear();
            String[] columns = {"id", "collection_id", "num", "agreement_id", "card_pan", "loan_type",
                    "last_update_user", "create_date", "create_user", "last_update_date", "iss_s", "s", "open_date",
                    "plan_close_date", "close_date", "interest_rate"};
            DB_Result res = Handler.selectItems("ACCOUNT", columns, null, Optional.of(0), Optional.of(200));
            if (res.getState().equals("200")) {
                NodeList clients = (NodeList) res.getResult();
                DBAccount account;
                for (int i = 0; i < clients.getLength(); i++) {
                    account = new DBAccount();
                    NodeList row = clients.item(i).getChildNodes();
                    for (int j = 0; j < row.getLength(); j++) {
                        if (row.item(j).getTextContent().length() == 0) continue;
                        switch (row.item(j).getNodeName()) {
                            case "id":
                                account.setId(Long.parseLong(row.item(j).getTextContent()));
                                break;
                            case "collection_id":
                                account.setCollection_id(Long.parseLong(row.item(j).getTextContent()));
                                break;
                            case "num":
                                account.setNum(row.item(j).getTextContent());
                                break;
                            case "agreement_id":
                                account.setAgreement_id(row.item(j).getTextContent());
                                break;
                            case "open_date":
                                account.setOpen_date(LocalDate.parse(row.item(j).getTextContent(), dateFormatter));
                                break;
                            case "plan_close_date":
                                account.setPlan_close_date(LocalDate.parse(row.item(j).getTextContent(), dateFormatter));
                                break;
                            case "close_date":
                                account.setClose_date(LocalDate.parse(row.item(j).getTextContent(), dateFormatter));
                                break;
                            case "card_pan":
                                account.setCard_pan(row.item(j).getTextContent());
                                break;
                            case "loan_type":
                                account.setLoan_type(Long.parseLong(row.item(j).getTextContent()));
                                break;
                            case "create_date":
                                account.setCreate_date(LocalDateTime.parse(row.item(j).getTextContent(), dateTimeFormatter));
                                break;
                            case "create_user":
                                account.setCreate_user(Long.parseLong(row.item(j).getTextContent()));
                                break;
                            case "last_update_date":
                                account.setLast_update_date(LocalDateTime.parse(row.item(j).getTextContent(), dateTimeFormatter));
                                break;
                            case "last_update_user":
                                account.setLast_update_user(Long.parseLong(row.item(j).getTextContent()));
                                break;
                            case "interest_rate":
                                account.setInterest_rate(Float.parseFloat(row.item(j).getTextContent()));
                            case "s":
                                account.setS(Float.parseFloat(row.item(j).getTextContent()));
                            case "iss_s":
                                account.setIss_s(Float.parseFloat(row.item(j).getTextContent()));
                        }
                    }
                    this.accounts.add(account);
                }
            }
        }
    }
}