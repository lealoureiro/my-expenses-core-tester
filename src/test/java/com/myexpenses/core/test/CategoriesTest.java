package com.myexpenses.core.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.models.Category;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.KeyData;
import com.myexpenses.core.test.models.SubCategory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Leandro Loureiro
 *
 */
public class CategoriesTest {

    private static final Logger LOGGER = LogManager.getLogger(CategoriesTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);

    private KeyData apiKey;
    private String sampleCategory;

    @BeforeClass
    public final void GetKey() throws Exception {
        LOGGER.info("Getting Key to start the tests...");
        this.apiKey = TestUtils.getNewKey(this.credentials);
    }

    @Test
    public void TestGetAllCategories() throws Exception {
        LOGGER.info("Getting all categories");

        final String resource = String.format("%s/categories/", GlobalSettings.SERVER);
        final HttpResponse<Category[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey.getKey())
                .asObject(Category[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        final Category[] categories = response.getBody();
        LOGGER.info(String.format("Fetched %d categories", categories.length));

        this.sampleCategory = categories[RANDOM_GENERATOR.nextInt(categories.length)].getName();
    }

    @Test
    public void AddNewCategory() throws Exception {

        final String resource = String.format("%s/categories/", GlobalSettings.SERVER);
        final String newCategoryName = String.format("Category %d", Math.abs(RANDOM_GENERATOR.nextLong() % 10000));
        final Category newCategory = new Category(newCategoryName);

        LOGGER.info(String.format("Adding new category with name %s", newCategoryName));

        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey.getKey())
                .body(newCategory)
                .asJson();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");
    }

    @Test(dependsOnMethods = "TestGetAllCategories")
    public void AddNewSubCategory() throws Exception {

        final String resource = String.format("%s/categories/%s/subcategories/", GlobalSettings.SERVER, this.sampleCategory);
        final String newSubCategoryName = String.format("Sub Category %d", Math.abs(RANDOM_GENERATOR.nextLong() % 10000));
        final SubCategory newSubCategory = new SubCategory(newSubCategoryName);

        LOGGER.info(String.format("Adding new sub category with name %s to category %s", newSubCategoryName, this.sampleCategory));

        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey.getKey())
                .body(newSubCategory)
                .asJson();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");
    }

}
