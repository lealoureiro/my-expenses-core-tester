package com.myexpenses.core.test.models;


/**
 * @author Leandro Loureiro
 */
public class Account {

    private String id;
    private String name;
    private String type;
    private Long balance;
    private Long startBalance;
    private String currency;
    private Integer transactions;

    public Account() {

    }

    public Account(final String name, final String type, final Long startBalance, final String currency) {
        this.name = name;
        this.type = type;
        this.startBalance = startBalance;
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

    public Long getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(final Long startBalance) {
        this.startBalance = startBalance;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(final Long balance) {
        this.balance = balance;
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
