package com.myexpenses.core.test.models;

/**
 * @author Leandro Loureiro
 *
 */
public class TransactionTimestamp {

    private long timestamp;


    public TransactionTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
}
