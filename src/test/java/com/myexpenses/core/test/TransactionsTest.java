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
 * @author Leandro Loureiro
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

        final String resource = String.format("%s/keys/", GlobalSettings.SERVER);
        final HttpResponse<KeyData> response = Unirest.post(resource)
                .header("Accept", "application/json")
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

        final String resource = String.format("%s/accounts/", GlobalSettings.SERVER);
        final HttpResponse<Account[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
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
        LOGGER.info(String.format("Account Start Balance: %.02f", account.getStartBalance() / 100.0));
        LOGGER.info(String.format("Account Currency: %s", account.getCurrency()));
        LOGGER.info(String.format("Account Type: %s", account.getType()));
    }


    @Test(dependsOnMethods = "GetSampleAccount")
    public final void GetAccountTransactions() throws Exception {

        LOGGER.info(String.format("Getting transactions for account %s ...", this.sampleAccountId));

        final String resource = String.format("%s/accounts/%s/transactions/", GlobalSettings.SERVER, this.sampleAccountId);
        final HttpResponse<Transaction[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
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
        final Long amount = RANDOM_GENERATOR.nextLong() % 10000;
        final Transaction transaction = new Transaction(description, "Personal", "Misc", System.currentTimeMillis(), amount, "single,sample");

        final String resource = String.format("%s/accounts/%s/transactions", GlobalSettings.SERVER, this.sampleAccountId);
        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Accept", "application/json")
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
