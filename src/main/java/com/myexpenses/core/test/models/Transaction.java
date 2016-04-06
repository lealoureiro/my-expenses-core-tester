package com.myexpenses.core.test.models;

/**
 * Created by leandro on 4/7/16.
 */
public class Transaction {

    private String id;
    private String desc;
    private String ctgId;
    private String subCtgId;
    private String datetime;
    private String amt;
    private String extRef;

    public Transaction() {

    }

    public Transaction(String id, String desc, String ctgId, String subCtgId, String datetime, String amt, String extRef) {
        this.id = id;
        this.desc = desc;
        this.ctgId = ctgId;
        this.subCtgId = subCtgId;
        this.datetime = datetime;
        this.amt = amt;
        this.extRef = extRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCtgId() {
        return ctgId;
    }

    public void setCtgId(String ctgId) {
        this.ctgId = ctgId;
    }

    public String getSubCtgId() {
        return subCtgId;
    }

    public void setSubCtgId(String subCtgId) {
        this.subCtgId = subCtgId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getExtRef() {
        return extRef;
    }

    public void setExtRef(String extRef) {
        this.extRef = extRef;
    }
}
