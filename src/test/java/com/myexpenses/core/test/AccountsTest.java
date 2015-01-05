package com.myexpenses.core.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leandro Loureiro on 13/11/14.
 * Version 0.0.1
 */
public class AccountsTest extends BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(AccountsTest.class);

    private String authToken;

    @BeforeClass
    public final void Login() throws Exception {

        final Map<String, Object> input = new HashMap<String, Object>();

        input.put("username", GlobalSettings.TEST_USER);
        input.put("password", GlobalSettings.TEST_PASSWORD);

        final HttpResponse response = sendRequest("auth/login", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONObject data = new JSONObject(response.getBody());

        final String token = data.getString("token");
        Assert.assertNotNull(token, "Invalid token!");
        this.authToken = token;

        final String userId = data.getString("id");
        Assert.assertNotNull(userId, "Invalid User Id!");
    }

    @Test
    public final void GetUserAccounts() throws Exception {

        LOGGER.info("Getting user accounts...");

        final Map<String, Object> input = new HashMap<String, Object>();
        input.put("token", this.authToken);

        final HttpResponse response = sendRequest("expenses/get_accounts", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONArray data = new JSONArray(response.getBody());

        for (int i = 0; i < data.length(); i++) {
            LOGGER.info(String.format("Account %d:", i));
            final JSONObject account = (JSONObject) data.get(i);
            LOGGER.info(String.format("Account ID: %s", account.get("acct")));
            LOGGER.info(String.format("Account name: %s", account.get("name")));
            LOGGER.info(String.format("Account balance: %.02f", account.getDouble("bal")));
            LOGGER.info(String.format("Start balance: %.02f", account.getDouble("startBal")));
            LOGGER.info(String.format("Account currency: %s", account.get("cur")));
            LOGGER.info(String.format("Account type: %s", account.get("type")));
        }
    }

    @AfterClass
    public final void Logout() throws Exception {
        final Map<String, Object> input = new HashMap<String, Object>();

        input.put("token", this.authToken);
        final HttpResponse response = sendRequest("auth/logout", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
    }

}
