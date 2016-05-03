package com.myexpenses.core.test.models;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by leandro on 4/7/16.
 */
public class Account {

    private String id;
    private String name;
    private String type;
    private Double balance;
    private Double startBalance;
    private String currency;
    private Integer transactions;

    public Account() {

    }

    public Account(final String name, final String type, final Double startBalance, final String currency) {
        this.name = name;
        this.type = type;
        this.startBalance = new BigDecimal(startBalance).setScale(2, RoundingMode.FLOOR).doubleValue();
        this.currency = currency;
    }


    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Double getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(final Double startBalance) {
        this.startBalance = startBalance;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(final Double balance) {
        this.balance = new BigDecimal(balance).setScale(2, RoundingMode.FLOOR).doubleValue();
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public Integer getTransactions() {
        return transactions;
    }

    public void setTransactions(final Integer transactions) {
        this.transactions = transactions;
    }
}
