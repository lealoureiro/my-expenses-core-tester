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

public final class ConnectivityTest {

    private static final Logger LOGGER = LogManager.getLogger(ConnectivityTest.class);
    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);

    private String apiKey;


    @Test
    public final void GetKey() throws Exception {
        LOGGER.info("Testing GetKey call ...");
        LOGGER.info(String.format("Using server %s", GlobalSettings.SERVER));

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

    @Test(dependsOnMethods = {"GetKey"})
    public final void DeleteKey() throws Exception {
        LOGGER.info("Testing Delete Key call ...");

        final String resource = String.format("%s/keys/", GlobalSettings.SERVER);
        final HttpResponse<JsonNode> response = Unirest.delete(resource)
                .header("Accept", "application/json")
                .header("authkey", this.apiKey)
                .asJson();
        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");
    }

}
