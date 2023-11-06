package ru.axe.abs.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class DBClient{
    public static final String entity_type = "CLIENT";
    private long id;
    private String first_name;
    private String last_name;
    private String middle_name;
    private LocalDate date_of_birth;
    private String pass;
    private String phone;
    private LocalDateTime create_date;
    private long create_user;
    private LocalDateTime last_update_date;
    private long last_update_user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreate_date() {
        return create_date;
    }

    public void setCreate_date(LocalDateTime create_date) {
        this.create_date = create_date;
    }

    public long getCreate_user() {
        return create_user;
    }

    public void setCreate_user(long create_user) {
        this.create_user = create_user;
    }

    public LocalDateTime getLast_update_date() {
        return last_update_date;
    }

    public void setLast_update_date(LocalDateTime last_update_date) {
        this.last_update_date = last_update_date;
    }

    public long getLast_update_user() {
        return last_update_user;
    }

    public void setLast_update_user(long last_update_user) {
        this.last_update_user = last_update_user;
    }

    public DBClient(long id, String first_name, String last_name, String middle_name, LocalDate date_of_birth, String pass,
                    String phone, LocalDateTime create_date, long create_user, LocalDateTime last_update_date, long last_update_user) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.middle_name = middle_name;
        this.date_of_birth = date_of_birth;
        this.pass = pass;
        this.phone = phone;
        this.create_date = create_date;
        this.create_user = create_user;
        this.last_update_date = last_update_date;
        this.last_update_user = last_update_user;
    }

    public DBClient() {

    }

    public DBClient(DBClient clone) {
        this.id = clone.getId();
        this.last_name = clone.getLast_name();
        this.first_name = clone.getFirst_name();
        this.middle_name = clone.getMiddle_name();
        this.pass = clone.getPass();
        this.phone = clone.getPhone();
        this.date_of_birth = clone.getDate_of_birth();
    }

    @Override
    public boolean equals(Object o) {
        DBClient client;
        try {
            client = (DBClient) o;
        } catch (Exception e) {
            return false;
        }

        return Objects.equals(this.pass, client.getPass())
                && Objects.equals(this.last_name, client.getLast_name())
                && Objects.equals(this.first_name, client.getFirst_name())
                && Objects.equals(this.middle_name, client.getMiddle_name())
                && this.date_of_birth.isEqual(client.getDate_of_birth())
                && Objects.equals(this.phone, client.getPhone());
    }
}
