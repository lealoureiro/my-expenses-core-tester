package com.myexpenses.core.test.models;

import java.util.List;

/**
 * @author Leandro Loureiro
 *
 */
public class Category {

    private String name;
    private List<String> subCategories;

    public Category() {

    }

    public Category(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<String> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(final List<String> subCategories) {
        this.subCategories = subCategories;
    }
}
