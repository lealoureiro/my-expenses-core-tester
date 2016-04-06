package com.myexpenses.core.test.models;

/**
 * Created by leandro on 4/6/16.
 */
public class KeyData {

    private String key;
    private String clientId;
    private String clientName;

    public KeyData() {

    }

    public KeyData(final String key, final String clientId, final String clientName) {
        this.key = key;
        this.clientId = clientId;
        this.clientName = clientName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }
}
