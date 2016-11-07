package com.myexpenses.core.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.KeyData;
import com.myexpenses.core.test.models.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Leandro Loureiro
 */
public class TagsTest {

    private static final Logger LOGGER = LogManager.getLogger(TagsTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);

    private KeyData apiKey;

    @BeforeClass
    public final void GetKey() throws Exception {
        LOGGER.info("Getting Key to start the tests...");
        apiKey = TestUtils.getNewKey(credentials);
    }

    @Test
    public void TestGetAllTags() throws Exception {
        LOGGER.info("Getting all tags");

        final Tag[] tags = getCurrentTags();
        LOGGER.info(String.format("Fetched %d tags", tags.length));

    }

    @Test
    public void AddNewTag() throws Exception {

        final String resource = String.format("%s/tags/", GlobalSettings.SERVER);
        final String newTagName = String.format("Tag %d", Math.abs(RANDOM_GENERATOR.nextLong() % 10000));
        final Tag newTag = new Tag(newTagName, true);

        LOGGER.info(String.format("Adding new tag with name %s", newTagName));

        final HttpResponse<String> response = Unirest.put(resource)
                .header("Content-type", "application/json")
                .header("authkey", apiKey.getKey())
                .body(newTag)
                .asString();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");

        final Tag[] tags = getCurrentTags();
        boolean added = false;

        for (int i = 0; i < tags.length && !added; i++) {
            if (newTagName.equals((tags[i].getName()))) {
                added = true;
            }
        }
        Assert.assertTrue(added, "New tag not stored!");
    }

    @Test
    public void DeleteTag() throws Exception {

        final Tag[] tags = getCurrentTags();
        if (tags.length == 0) {
            throw new SkipException("Not tags found to perform the test!");
        }

        final String sampleTag = tags[RANDOM_GENERATOR.nextInt(tags.length)].getName();

        final String resource = String.format("%s/tags/%s", GlobalSettings.SERVER, sampleTag);
        LOGGER.info(String.format("Deleting tag %s", sampleTag));
        HttpResponse<String> response = Unirest.delete(resource)
                .header("authkey", apiKey.getKey())
                .asString();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");

        response = Unirest.delete(resource)
                .header("authkey", apiKey.getKey())
                .asString();

        Assert.assertEquals(response.getStatus(), 404, "Invalid HTTP code!");
    }

    private Tag[] getCurrentTags() throws Exception {
        final String resource = String.format("%s/tags/", GlobalSettings.SERVER);
        final HttpResponse<Tag[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey.getKey())
                .asObject(Tag[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        return response.getBody();
    }

}
