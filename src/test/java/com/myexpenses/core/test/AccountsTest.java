package com.myexpenses.core.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.models.Account;
import com.myexpenses.core.test.models.Transaction;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.KeyData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Created by Leandro Loureiro on 13/11/14.
 * Version 0.0.1
 */
public class AccountsTest {

    private static final Logger LOGGER = LogManager.getLogger(AccountsTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);
    private String apiKey;

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


        final String resource = String.format("http://%s:%s/expenses/get_accounts", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        final HttpResponse<Account[]> response = Unirest.post(resource)
                .field("token", this.apiKey)
                .asObject(Account[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final Account[] accounts = response.getBody();

        for (int i = 0; i < accounts.length; i++) {
            LOGGER.info(String.format("Account %d:", i));
            LOGGER.info(String.format("Account ID: %s", accounts[i].getAcct()));
            LOGGER.info(String.format("Account name: %s", accounts[i].getName()));
            LOGGER.info(String.format("Account balance: %s", accounts[i].getBal()));
            LOGGER.info(String.format("Account Start balance: %s", accounts[i].getStartBal()));
            LOGGER.info(String.format("Account currency: %s", accounts[i].getCur()));
            LOGGER.info(String.format("Account type: %s", accounts[i].getType()));
        }
    }

    /*
    @Test
    public final void CreateAccount() throws Exception {

        LOGGER.info("Creating new account...");

        final Map<String, Object> input = new HashMap<String, Object>();
        input.put("token", this.authToken);
        input.put("name", "Sample Transaction " + Math.abs(RANDOM_GENERATOR.nextInt()));
        input.put("type", "Current Transaction");
        input.put("startBalance", "" + Math.random());
        input.put("currency", "EUR");

        final HttpResponse response = sendRequest("expenses/add_account", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONObject data = new JSONObject(response.getBody());

    }

    @AfterClass
    public final void Logout() throws Exception {
        final Map<String, Object> input = new HashMap<String, Object>();

        input.put("token", this.authToken);
        final HttpResponse response = sendRequest("auth/logout", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
    }
 */
}
