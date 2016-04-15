package com.myexpenses.core.test;


import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.KeyData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.Random;

public final class ConnectivityTest {

    private static final Logger LOGGER = LogManager.getLogger(ConnectivityTest.class);
    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);


    private String apiKey;
    private static final Random RANDOM_GENERATOR = new SecureRandom();


    @Test
    public final void GetKey() throws Exception {
        LOGGER.info("Testing GetKey call ...");

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

    @Test(dependsOnMethods = {"GetKey"})
    public final void DeleteKey() throws Exception {

        LOGGER.info("Testing Delete Key call ...");
        final String resource = String.format("http://%s:%s/keys/", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        final HttpResponse<JsonNode> response = Unirest.delete(resource)
                .header("authkey", this.apiKey)
                .asJson();
        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");
    }


}
