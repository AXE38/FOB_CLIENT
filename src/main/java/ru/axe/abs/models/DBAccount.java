package ru.axe.abs.models;

import ru.axe.abs.Handler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class DBAccount {
    public static final String entity_type = "ACCOUNT";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(long collection_id) {
        this.collection_id = collection_id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getAgreement_id() {
        return agreement_id;
    }

    public void setAgreement_id(String agreement_id) {
        this.agreement_id = agreement_id;
    }

    public String getCard_pan() {
        return card_pan;
    }

    public void setCard_pan(String card_pan) {
        this.card_pan = card_pan;
    }

    public String getLoan_type() {
        return Handler.getDictRow(this.loan_type).getDescription();
    }

    public void setLoan_type(long loan_type) {
        this.loan_type = loan_type;
    }

    public float getIss_s() {
        return iss_s;
    }

    public void setIss_s(float iss_s) {
        this.iss_s = iss_s;
    }

    public float getS() {
        return s;
    }

    public void setS(float s) {
        this.s = s;
    }

    public LocalDate getOpen_date() {
        return open_date;
    }

    public void setOpen_date(LocalDate open_date) {
        this.open_date = open_date;
    }

    public LocalDate getPlan_close_date() {
        return plan_close_date;
    }

    public void setPlan_close_date(LocalDate plan_close_date) {
        this.plan_close_date = plan_close_date;
    }

    public LocalDate getClose_date() {
        return close_date;
    }

    public void setClose_date(LocalDate close_date) {
        this.close_date = close_date;
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

    private long id;
    private long collection_id;
    private String num;
    private String agreement_id;
    private String card_pan;
    private long loan_type;
    private float iss_s;
    private float s;
    private LocalDate open_date;
    private LocalDate plan_close_date;
    private LocalDate close_date;
    private LocalDateTime create_date;
    private long create_user;
    private LocalDateTime last_update_date;
    private long last_update_user;

    public float getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(float interest_rate) {
        this.interest_rate = interest_rate;
    }

    public long getLoan_type_id() {
        return this.loan_type;
    }

    private float interest_rate;
    public DBAccount() {

    }

    public DBAccount(DBAccount clone) {
        this.id = clone.getId();
        this.collection_id = clone.getCollection_id();
        this.num = clone.getNum();
        this.agreement_id = clone.getAgreement_id();
        this.card_pan = clone.getCard_pan();
        this.loan_type = clone.loan_type;
        this.iss_s = clone.getIss_s();
        this.s = clone.getS();
        this.open_date = clone.getOpen_date();
        this.plan_close_date = clone.getPlan_close_date();
        this.close_date = clone.getClose_date();
        this.interest_rate = clone.getInterest_rate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBAccount dbAccount = (DBAccount) o;
        return collection_id == dbAccount.collection_id
                && loan_type == dbAccount.loan_type
                && Float.compare(dbAccount.iss_s, iss_s) == 0
                && Float.compare(dbAccount.s, s) == 0
                && Objects.equals(num, dbAccount.num)
                && Objects.equals(agreement_id, dbAccount.agreement_id)
                && Objects.equals(card_pan, dbAccount.card_pan)
                && Objects.equals(open_date, dbAccount.open_date)
                && Objects.equals(plan_close_date, dbAccount.plan_close_date)
                && Objects.equals(close_date, dbAccount.close_date)
                && Float.compare(dbAccount.interest_rate, interest_rate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(collection_id, num, agreement_id, card_pan, loan_type, iss_s, s, open_date, plan_close_date, close_date, interest_rate);
    }
}
