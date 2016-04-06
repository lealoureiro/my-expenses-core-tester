package com.myexpenses.core.test.models;

/**
 * Created by leandro on 4/7/16.
 */
public class Account {

    private String acct;
    private String name;
    private String type;
    private String startBal;
    private String cur;
    private String bal;

    public Account() {

    }

    public Account(final String acct, final String name, final String type, final String startBal, final String cur, final String bal) {
        this.acct = acct;
        this.name = name;
        this.type = type;
        this.startBal = startBal;
        this.bal = bal;
    }

    public String getAcct() {
        return acct;
    }

    public void setAcct(final String acct) {
        this.acct = acct;
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

    public String getStartBal() {
        return startBal;
    }

    public void setStartBal(final String startBal) {
        this.startBal = startBal;
    }

    public String getCur() {
        return cur;
    }

    public void setCur(final String cur) {
        this.cur = cur;
    }

    public String getBal() {
        return bal;
    }

    public void setBal(final String bal) {
        this.bal = bal;
    }
}
