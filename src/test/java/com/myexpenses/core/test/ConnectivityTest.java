package com.myexpenses.core.test;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class ConnectivityTest extends BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(ConnectivityTest.class);


    private String authToken;
    private static final Random RANDOM_GENERATOR = new SecureRandom();


    @Test
    public final void Login() throws Exception {

        LOGGER.info("Testing Login feature...");

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

    @Test(dependsOnMethods = {"Login"})
    public final void ServerEcho() throws Exception {

        LOGGER.info("Testing Core Server Echo...");

        final Map<String, Object> input = new HashMap<String, Object>();
        final String randomString = String.format("Server Echo Test %d", RANDOM_GENERATOR.nextInt());


        input.put("token", this.authToken);
        input.put("echo", randomString);

        final HttpResponse response = sendRequest("expenses/echo", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
        final JSONObject data = new JSONObject(response.getBody());
        Assert.assertEquals(data.get("echo"), randomString, "Invalid echo!");

    }

    @Test(dependsOnMethods = {"ServerEcho"})
    public final void Logout() throws Exception{

        LOGGER.info("Testing Logout feature...");

        final Map<String, Object> input = new HashMap<String, Object>();

        input.put("token", this.authToken);
        final HttpResponse response = sendRequest("auth/logout", input);
        Assert.assertEquals(response.getCode(), 200, "Invalid HTTP code!");
    }

    @Test(dependsOnMethods = {"Logout"})
    public final void CheckOutdatedToken() throws Exception{

        LOGGER.info("Testing usage of invalid authentication token...");

        final Map<String, Object> input = new HashMap<String, Object>();

        final String randomString = String.format("Server Echo Test %d", RANDOM_GENERATOR.nextInt());
        input.put("token", this.authToken);
        input.put("echo", randomString);

        final HttpResponse response = sendRequest("expenses/echo", input);
        Assert.assertEquals(response.getCode(), 403, "Invalid HTTP code!");
    }


}
