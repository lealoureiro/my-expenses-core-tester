package com.myexpenses.core.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.*;

/**
 * Created by Leandro Loureiro on 13/11/14.
 * Version 0.0.1
 */
public class TransactionsTest extends BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(TransactionsTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private String authToken;
    private String sampleAccountId;
    private Set<String> categories;
    private Map<String, String> subCategories;

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
    }

    @AfterClass
    public final void Logout() throws Exception {
        final Map<String, Object> input = new HashMap<String, Object>();

        input.put("token", this.authToken);
        final HttpResponse response = sendRequest("auth/logout", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
    }


    @Test
    public final void GetSampleAccount() throws Exception {

        LOGGER.info("Getting sample account...");

        final Map<String, Object> input = new HashMap<String, Object>();
        input.put("token", this.authToken);

        final HttpResponse response = sendRequest("expenses/get_accounts", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONArray data = new JSONArray(response.getBody());

        if (data.length() == 0) {
            throw new SkipException("No accounts found!");
        }

        final JSONObject account = (JSONObject) data.get(RANDOM_GENERATOR.nextInt(data.length()));
        sampleAccountId = account.getString("acct");

        Assert.assertTrue(isUUID(sampleAccountId), "Invalid account ID!");

        LOGGER.info(String.format("Account ID: %s", sampleAccountId));
        LOGGER.info(String.format("Account name: %s", account.get("name")));
        LOGGER.info(String.format("Account balance: %.02f", account.getDouble("bal")));
        LOGGER.info(String.format("Start balance: %.02f", account.getDouble("startBal")));
        LOGGER.info(String.format("Account currency: %s", account.get("cur")));
        LOGGER.info(String.format("Account type: %s", account.get("type")));
    }

    @Test(dependsOnMethods = "GetSampleAccount")
    public final void GetAccountTransactions() throws Exception {

        LOGGER.info(String.format("Getting transactions for account %s ...", this.sampleAccountId));

        final Map<String, Object> input = new HashMap<String, Object>();
        input.put("token", this.authToken);
        input.put("acct", this.sampleAccountId);

        final HttpResponse response = sendRequest("expenses/get_transactions", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONArray data = new JSONArray(response.getBody());
        LOGGER.info(String.format("Fetched %d transactions", data.length()));
    }

    @Test
    public final void GetCategories() throws Exception {

        LOGGER.info("Getting Categories and Sub Categories...");

        final Map<String, Object> input = new HashMap<String, Object>();
        input.put("token", this.authToken);

        final HttpResponse response = sendRequest("expenses/get_categories", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONObject data = new JSONObject(response.getBody());

        final JSONArray categories = (JSONArray) data.get("Categories");
        final JSONArray subCategories = (JSONArray) data.get("SubCategories");
        LOGGER.info(String.format("Fetched %d categories", categories.length()));
        LOGGER.info(String.format("Fetched %d sub categories", subCategories.length()));


        this.categories = new HashSet<String>();
        this.subCategories = new LinkedHashMap<String, String>();

        for (int i = 0; i < categories.length(); i++) {
            final JSONObject category = categories.getJSONObject(i);
            this.categories.add(category.getString("name"));
        }

        for (int i = 0; i < subCategories.length(); i++) {
            final JSONObject subCategory = subCategories.getJSONObject(i);
            this.subCategories.put(subCategory.getString("subCat"), subCategory.getString("cat"));
        }

        LOGGER.info("Categories fetched:");
        for (String s : this.categories) {
            LOGGER.info(s);
        }

        LOGGER.info("Sub Categories fetched:");
        for (Map.Entry<String, String> e : this.subCategories.entrySet()) {
            LOGGER.info(String.format("%s - %s", e.getKey(), e.getValue()));
        }

    }

    @Test(dependsOnMethods = "GetSampleAccount")
    public final void AddSingleTransaction() throws Exception {
        LOGGER.info("Adding sample transaction...");

        final Map<String, Object> input = new HashMap<String, Object>();
        input.put("token", this.authToken);
        input.put("acct", this.sampleAccountId);
        input.put("dsc", String.format("Sample Transaction %d", Math.abs(RANDOM_GENERATOR.nextInt())));
        input.put("cat", "Personal");
        input.put("subCat", "Misc");
        input.put("amt", "" + Math.random());
        input.put("timestamp", "" + System.currentTimeMillis());
        input.put("tags", "single,sample");

        final HttpResponse response = sendRequest("expenses/add_transaction", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONObject data = new JSONObject(response.getBody());


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
