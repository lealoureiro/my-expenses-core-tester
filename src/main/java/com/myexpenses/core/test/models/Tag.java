package com.myexpenses.core.test.models;

/**
 * @author Leandro Loureiro
 */
public class Tag {

    private String name;
    private boolean defaultSelected;


    public Tag(final String name, final boolean defaultSelected) {
        this.name = name;
        this.defaultSelected = defaultSelected;
    }

    public Tag() {
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isDefaultSelected() {
        return defaultSelected;
    }

    public void setDefaultSelected(final boolean defaultSelected) {
        this.defaultSelected = defaultSelected;
    }
}
