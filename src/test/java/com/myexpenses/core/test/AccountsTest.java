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
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * @author Leandro Loureiro
 */
public class AccountsTest {

    private static final Logger LOGGER = LogManager.getLogger(AccountsTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);
    private String apiKey;
    private String sampleAccountId;

    @BeforeClass
    public final void GetKey() throws Exception {

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
    public final void GetUserAccounts() throws Exception {

        LOGGER.info("Getting user accounts...");

        final String resource = String.format("%s/accounts/", GlobalSettings.SERVER);
        final HttpResponse<Account[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", this.apiKey)
                .asObject(Account[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final Account[] accounts = response.getBody();

        for (int i = 0; i < accounts.length; i++) {
            LOGGER.info(String.format("Account %d:", i));
            LOGGER.info(String.format("Account ID: %s", accounts[i].getId()));
            LOGGER.info(String.format("Account Name: %s", accounts[i].getName()));
            LOGGER.info(String.format("Account Start balance: %.02f", accounts[i].getStartBalance() / 100.0));
            LOGGER.info(String.format("Account Currency: %s", accounts[i].getCurrency()));
            LOGGER.info(String.format("Account Type: %s", accounts[i].getType()));
        }
        LOGGER.info(String.format("Fetched %d accounts", accounts.length));

        if (accounts.length > 0) {
            final Account account = accounts[RANDOM_GENERATOR.nextInt(accounts.length)];
            Assert.assertTrue(isUUID(account.getId()), "Invalid account ID!");
            sampleAccountId = account.getId();
        } else {
            throw new SkipException("No accounts found!");
        }
    }


    @Test
    public final void CreateAccount() throws Exception {

        LOGGER.info("Creating new account...");

        final String accountName = String.format("Sample Account %d", Math.abs(RANDOM_GENERATOR.nextInt()));
        final Long startBalance = Math.abs(RANDOM_GENERATOR.nextLong() % 100000);
        final Account account = new Account(accountName, "current Account", startBalance, "EUR");

        final String resource = String.format("%s/accounts/", GlobalSettings.SERVER);
        HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey)
                .body(account)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
    }

    @Test(dependsOnMethods = "GetUserAccounts")
    public void GetSampleAccountInformation() throws Exception {

        final String resource = String.format("%s/accounts/%s", GlobalSettings.SERVER, sampleAccountId);
        final HttpResponse<Account> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey)
                .asObject(Account.class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final Account account = response.getBody();
        LOGGER.info(String.format("Account ID: %s", account.getId()));
        LOGGER.info(String.format("Account Name: %s", account.getName()));
        LOGGER.info(String.format("Account Start balance: %.02f", account.getStartBalance() / 100.0));
        LOGGER.info(String.format("Account balance: %.02f", account.getBalance() / 100.0));
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
