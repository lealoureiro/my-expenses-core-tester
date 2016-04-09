package com.myexpenses.core.test.models;

/**
 * Created by leandro on 4/7/16.
 */
public class Account {

    private String id;
    private String name;
    private String type;
    private double balance;
    private double startBalance;
    private String currency;


    public Account() {

    }

    public Account(final String name, final String type, final double startBalance, final String currency) {
        this.name = name;
        this.type = type;
        this.startBalance = startBalance;
        this.currency = currency;
    }

    public Account(final String id, final String name, final String type, final double startBalance, final String currency, final double balance) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.startBalance = startBalance;
        this.balance = balance;
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

    public double getStartBalance() {
        return startBalance;
    }

    public void setStartBalance(final double startBalance) {
        this.startBalance = startBalance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(final double balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }


}
