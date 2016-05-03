package com.myexpenses.core.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.models.Account;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.KeyData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Leandro Loureiro on 13/11/14.
 * Version 0.0.1
 */
public class AccountsTest {

    private static final Logger LOGGER = LogManager.getLogger(AccountsTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);
    private String apiKey;
    private String sampleAccountId;

    @BeforeClass
    public final void GetKey() throws Exception {

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
    public final void GetUserAccounts() throws Exception {

        LOGGER.info("Getting user accounts...");

        final String resource = String.format("http://%s:%s/accounts/", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        final HttpResponse<Account[]> response = Unirest.get(resource)
                .header("authkey", this.apiKey)
                .asObject(Account[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final Account[] accounts = response.getBody();

        for (int i = 0; i < accounts.length; i++) {
            LOGGER.info(String.format("Account %d:", i));
            LOGGER.info(String.format("Account ID: %s", accounts[i].getId()));
            LOGGER.info(String.format("Account Name: %s", accounts[i].getName()));
            LOGGER.info(String.format("Account Start balance: %s", accounts[i].getStartBalance()));
            LOGGER.info(String.format("Account Currency: %s", accounts[i].getCurrency()));
            LOGGER.info(String.format("Account Type: %s", accounts[i].getType()));
        }
        LOGGER.info(String.format("Fetched %d accounts", accounts.length));

        final Account account = accounts[RANDOM_GENERATOR.nextInt(accounts.length)];
        Assert.assertTrue(isUUID(account.getId()), "Invalid account ID!");
        sampleAccountId = account.getId();
    }


    @Test
    public final void CreateAccount() throws Exception {

        LOGGER.info("Creating new account...");

        final String accountName = String.format("Sample Account %d", Math.abs(RANDOM_GENERATOR.nextInt()));
        final Account account = new Account(accountName, "current Account", Math.random(), "EUR");

        final String resource = String.format("http://%s:%s/accounts/", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey)
                .body(account)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
    }

    @Test(dependsOnMethods = "GetUserAccounts")
    public void GetSampleAccountInformation() throws Exception {

        final String resource = String.format("http://%s:%s/accounts/%s", GlobalSettings.HOSTNAME, GlobalSettings.PORT, this.sampleAccountId);
        final HttpResponse<Account> response = Unirest.get(resource)
                .header("authkey", this.apiKey)
                .asObject(Account.class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final Account account = response.getBody();
        LOGGER.info(String.format("Account ID: %s", account.getId()));
        LOGGER.info(String.format("Account Name: %s", account.getName()));
        LOGGER.info(String.format("Account Start balance: %s", account.getStartBalance()));
        LOGGER.info(String.format("Account balance: %s", account.getBalance()));
        LOGGER.info(String.format("Account Currency: %s", account.getCurrency()));
        LOGGER.info(String.format("Account Type: %s", account.getType()));
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
