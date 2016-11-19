package com.myexpenses.core.test.models;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Leandro Loureiro
 */
public class Transaction {

    private String id;
    private String description;
    private String category;
    private String subCategory;
    private long timestamp;
    private Long amount;
    private String externalReference;
    private List<String> tags;

    public Transaction() {

    }

    public Transaction(final String description, final String category, final String subCategory, final long timestamp, final Long amount) {
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.timestamp = timestamp;
        this.amount = amount;
        this.tags = new ArrayList<>();
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

    public Long getAmount() {
        return amount;
    }

    public void setAmount(final Long amount) {
        this.amount = amount;
    }

    public String getExternalRefernce() {
        return externalReference;
    }

    public void setExternalReference(final String externalReference) {
        this.externalReference = externalReference;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }
}
