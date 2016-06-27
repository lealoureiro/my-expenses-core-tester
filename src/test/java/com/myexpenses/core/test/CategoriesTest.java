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
 */
public class CategoriesTest {

    private static final Logger LOGGER = LogManager.getLogger(CategoriesTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);

    private KeyData apiKey;
    private String sampleCategory1;
    private String sampleCategory2;

    @BeforeClass
    public final void GetKey() throws Exception {
        LOGGER.info("Getting Key to start the tests...");
        this.apiKey = TestUtils.getNewKey(this.credentials);
    }

    @Test
    public void TestGetAllCategories() throws Exception {
        LOGGER.info("Getting all categories");

        final Category[] categories = getCurrentCategories();
        LOGGER.info(String.format("Fetched %d categories", categories.length));

        this.sampleCategory1 = categories[RANDOM_GENERATOR.nextInt(categories.length)].getName();
        this.sampleCategory2 = categories[RANDOM_GENERATOR.nextInt(categories.length)].getName();
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

        final Category[] categories = getCurrentCategories();
        boolean added = false;

        for (int i = 0; i < categories.length && !added; i++) {
            if (newCategoryName.equals((categories[i].getName()))) {
                added = true;
            }
        }
        Assert.assertTrue(added, "New category not stored!");
    }

    @Test(dependsOnMethods = "TestGetAllCategories")
    public void AddNewSubCategory() throws Exception {

        final String resource = String.format("%s/categories/%s/subcategories/", GlobalSettings.SERVER, this.sampleCategory1);
        final String newSubCategoryName = String.format("Sub Category %d", Math.abs(RANDOM_GENERATOR.nextLong() % 10000));
        final SubCategory newSubCategory = new SubCategory(newSubCategoryName);

        LOGGER.info(String.format("Adding new sub category with name %s to category %s", newSubCategoryName, this.sampleCategory1));

        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey.getKey())
                .body(newSubCategory)
                .asJson();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");

        final Category[] categories = getCurrentCategories();
        boolean added = false;

        for (int i = 0; i < categories.length && !added; i++) {
            if (this.sampleCategory1.equals((categories[i].getName()))) {
                for (final String subCategory : categories[i].getSubCategories()) {
                    if (newSubCategory.getName().equals(subCategory)) {
                        added = true;
                    }
                }
            }
        }
        Assert.assertTrue(added, "New sub category not stored!");
    }

    @Test(dependsOnMethods = "AddNewSubCategory")
    public void DeleteEntireCategory() throws Exception {

        final String resource = String.format("%s/categories/%s", GlobalSettings.SERVER, this.sampleCategory2);

        LOGGER.info(String.format("Deleting entire category %s and its sub categories", this.sampleCategory2));

        HttpResponse<JsonNode> response = Unirest.delete(resource)
                .header("authkey", this.apiKey.getKey())
                .asJson();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");

        response = Unirest.delete(resource)
                .header("authkey", this.apiKey.getKey())
                .asJson();

        Assert.assertEquals(response.getStatus(), 404, "Invalid HTTP code!");

    }

    private Category[] getCurrentCategories() throws Exception {
        final String resource = String.format("%s/categories/", GlobalSettings.SERVER);
        final HttpResponse<Category[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey.getKey())
                .asObject(Category[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        return response.getBody();
    }

}
