package ru.axe.abs.models;

public class DBDict {
    public long getId() {
        return id;
    }

    public String getGroup_code() {
        return group_code;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private long id;
    private String group_code;
    private String code;
    private String description;

    public DBDict(long id, String group_code, String code, String description) {
        this.id = id;
        this.group_code = group_code;
        this.code = code;
        this.description = description;
    }

    public DBDict() {

    }

    @Override
    public String toString() {
        return this.description;
    }
}
