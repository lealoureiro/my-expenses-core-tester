package com.myexpenses.core.test.models;

/**
 * Created by leandro on 4/7/16.
 */
public class Transaction {

    private String id;
    private String description;
    private String category;
    private String subCategory;
    private long timestamp;
    private double amount;
    private String externalReference;
    private String tags;

    public Transaction() {

    }

    public Transaction(final String id, final String description, final String category, final String subCategory, final long timestamp, final double amount, final String externalReference) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.timestamp = timestamp;
        this.amount = amount;
        this.externalReference = externalReference;
    }

    public Transaction(final String description, final String category, final String subCategory, final long timestamp, final double amount, final String tags) {
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.timestamp = timestamp;
        this.amount = amount;
        this.tags = tags;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(final String subCategoryId) {
        this.subCategory = subCategory;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }

    public String getExternalRefernce() {
        return externalReference;
    }

    public void setExternalReference(final String externalReference) {
        this.externalReference = externalReference;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(final String tags) {
        this.tags = tags;
    }
}
