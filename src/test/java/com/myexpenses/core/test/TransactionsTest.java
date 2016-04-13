package com.myexpenses.core.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.models.Account;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.KeyData;
import com.myexpenses.core.test.models.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.*;

/**
 * Created by Leandro Loureiro on 13/11/14.
 * Version 0.0.1
 */
public class TransactionsTest {

    private static final Logger LOGGER = LogManager.getLogger(TransactionsTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);

    private String apiKey;
    private String sampleAccountId;


    @BeforeClass
    public final void GetKey() throws Exception {

        LOGGER.info("Getting Key to start the tests...");

        final String resource = String.format("http://%s:%s/keys/", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        final HttpResponse<KeyData> response = Unirest.post(resource)
                .header("accept", "application/json")
                .header("Content-type", "application/json")
                .body(credentials)
                .asObject(KeyData.class);

        final KeyData newKeyData = response.getBody();
        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP status code!");
        LOGGER.info(String.format("Got client ID %s", newKeyData.getClientId()));
        LOGGER.info(String.format("Got client name %s", newKeyData.getClientName()));
        LOGGER.info(String.format("Got new key %s", newKeyData.getKey()));
        this.apiKey = newKeyData.getKey();
    }


    @Test
    public final void GetSampleAccount() throws Exception {

        LOGGER.info("Getting sample account...");

        final String resource = String.format("http://%s:%s/accounts/", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        final HttpResponse<Account[]> response = Unirest.get(resource)
                .header("authkey", this.apiKey)
                .asObject(Account[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final Account[] accounts = response.getBody();
        if (accounts.length == 0) {
            throw new SkipException("No accounts found to execute test!");
        }

        final Account account = accounts[RANDOM_GENERATOR.nextInt(accounts.length)];

        Assert.assertTrue(isUUID(account.getId()), "Invalid account ID!");
        sampleAccountId = account.getId();

        LOGGER.info(String.format("Account ID: %s", sampleAccountId));
        LOGGER.info(String.format("Account Name: %s", account.getName()));
        LOGGER.info(String.format("Account Balance: %s", account.getBalance()));
        LOGGER.info(String.format("Account Start Balance: %s", account.getStartBalance()));
        LOGGER.info(String.format("Account Currency: %s", account.getCurrency()));
        LOGGER.info(String.format("Account Type: %s", account.getType()));
    }


    @Test(dependsOnMethods = "GetSampleAccount")
    public final void GetAccountTransactions() throws Exception {

        LOGGER.info(String.format("Getting transactions for account %s ...", this.sampleAccountId));

        final String resource = String.format("http://%s:%s/accounts/%s/transactions/", GlobalSettings.HOSTNAME, GlobalSettings.PORT, this.sampleAccountId);
        final HttpResponse<Transaction[]> response = Unirest.get(resource)
                .header("authkey", apiKey)
                .asObject(Transaction[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        final Transaction[] transactions = response.getBody();
        LOGGER.info(String.format("Fetched %d transactions", transactions.length));
    }


    @Test(dependsOnMethods = "GetSampleAccount")
    public final void AddTransactions() throws Exception {
        LOGGER.info("Adding transaction test...");

        final String description = String.format("Sample Transaction %d", Math.abs(RANDOM_GENERATOR.nextInt()));

        final Transaction transaction = new Transaction(description, "Personal", "Misc", System.currentTimeMillis(), 1000.0 * Math.random(), "single,sample");

        final String resource = String.format("http://%s:%s/accounts/%s/transactions", GlobalSettings.HOSTNAME, GlobalSettings.PORT, this.sampleAccountId);
        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey)
                .body(transaction)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final JsonNode data = response.getBody();
        final String transactionId = data.getObject().get("id").toString();
        Assert.assertTrue(isUUID(transactionId), "Invalid Transaction ID!");
        LOGGER.info(String.format("Transaction %s added to account %s", transactionId, this.sampleAccountId));
    }

    private static boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


}
