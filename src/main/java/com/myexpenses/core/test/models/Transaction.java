package com.myexpenses.core.test.models;

/**
 * Created by leandro on 4/7/16.
 */
public class Transaction {

    private String id;
    private String description;
    private String categoryId;
    private String subCategoryId;
    private String datetime;
    private String amount;
    private String externalReference;

    public Transaction() {

    }

    public Transaction(final String id, final String description, final String categoryId, final String subCategoryId, final String datetime, final String amount, final String externalReference) {
        this.id = id;
        this.description = description;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.datetime = datetime;
        this.amount = amount;
        this.externalReference = externalReference;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(final String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(final String datetime) {
        this.datetime = datetime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(final String amount) {
        this.amount = amount;
    }

    public String getExternalRefernce() {
        return externalReference;
    }

    public void setExternalReference(final String externalReference) {
        this.externalReference = externalReference;
    }
}
