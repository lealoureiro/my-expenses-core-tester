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
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.util.List;
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

    @Test
    public void AddNewSubCategory() throws Exception {

        Category[] categories = getCurrentCategories();
        if (categories.length == 0) {
            throw new SkipException("Not categories found to perform the test!");
        }

        final String sampleCategory = categories[RANDOM_GENERATOR.nextInt(categories.length)].getName();

        final String resource = String.format("%s/categories/%s/subcategories/", GlobalSettings.SERVER, sampleCategory);
        final String newSubCategoryName = String.format("Sub Category %d", Math.abs(RANDOM_GENERATOR.nextLong() % 10000));
        final SubCategory newSubCategory = new SubCategory(newSubCategoryName);

        LOGGER.info(String.format("Adding new sub category with name %s to category %s", newSubCategoryName, sampleCategory));

        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey.getKey())
                .body(newSubCategory)
                .asJson();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");

        categories = getCurrentCategories();
        boolean added = false;

        for (int i = 0; i < categories.length && !added; i++) {
            if (sampleCategory.equals((categories[i].getName()))) {
                for (final String subCategory : categories[i].getSubCategories()) {
                    if (newSubCategory.getName().equals(subCategory)) {
                        added = true;
                    }
                }
            }
        }
        Assert.assertTrue(added, "New sub category not stored!");
    }

    @Test
    public void DeleteEntireCategory() throws Exception {

        Category[] categories = getCurrentCategories();
        if (categories.length == 0) {
            throw new SkipException("Not categories found to perform the test!");
        }

        final String sampleCategory = categories[RANDOM_GENERATOR.nextInt(categories.length)].getName();

        final String resource = String.format("%s/categories/%s", GlobalSettings.SERVER, sampleCategory);
        LOGGER.info(String.format("Deleting entire category %s and its sub categories", sampleCategory));
        HttpResponse<JsonNode> response = Unirest.delete(resource)
                .header("authkey", this.apiKey.getKey())
                .asJson();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");

        response = Unirest.delete(resource)
                .header("authkey", this.apiKey.getKey())
                .asJson();

        Assert.assertEquals(response.getStatus(), 404, "Invalid HTTP code!");
    }

    @Test
    public void DeleteSubCategory() throws Exception {

        Category[] categories = getCurrentCategories();
        if (categories.length == 0) {
            throw new SkipException("Not categories found to perform the test!");
        }

        boolean found = false;
        int i = 0;
        do {
            if (categories[i].getSubCategories().size() > 0) {
                found = true;
            } else {
                i++;
            }
        }
        while (!found && i < categories.length);

        if (!found) {
            throw new SkipException("No sub categories found to perform the test");
        }

        final List<String> subCategories = categories[i].getSubCategories();
        final String sampleCategory = categories[i].getName();
        final String subCategory = subCategories.get(RANDOM_GENERATOR.nextInt(subCategories.size()));

        final String resource = String.format("%s/categories/%s/subcategories/%s", GlobalSettings.SERVER, sampleCategory, subCategory);
        LOGGER.info(String.format("Deleting entire category %s and its sub categories", sampleCategory));
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
