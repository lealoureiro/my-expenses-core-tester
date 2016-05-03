package com.myexpenses.core.test.concurrent;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.GlobalSettings;
import com.myexpenses.core.test.models.Account;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.KeyData;
import com.myexpenses.core.test.models.Transaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Leandro Loureiro
 */
public class ConcurrentTest {

    private static final Logger LOGGER = LogManager.getLogger(ConcurrentTest.class);

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);
    private String apiKey;


    @BeforeClass
    public void getAccessKey() throws Exception {
        LOGGER.info("Getting access key to proceed with test");

        final String resource = String.format("http://%s:%s/keys/", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        final HttpResponse<KeyData> response = Unirest.post(resource)
                .header("accept", "application/json")
                .header("Content-type", "application/json")
                .body(credentials)
                .asObject(KeyData.class);

        final KeyData newKeyData = response.getBody();
        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP status code!");
        this.apiKey = newKeyData.getKey();
    }

    @Test(threadPoolSize = 4, invocationCount = 4)
    public void concurrentUsageScenario() throws Exception {

        final long userId = Thread.currentThread().getId();
        final String accountName = String.format("Account %010d", Math.abs(ThreadLocalRandom.current().nextInt()));
        final Double startBalance = ThreadLocalRandom.current().nextDouble();
        final Account account = new Account(accountName, "current account", startBalance, "EUR");

        LOGGER.info(String.format("User %d created account %s with start balance %.02f€", userId, accountName, account.getStartBalance()));

        final String accountId = addAccount(account);
        account.setId(accountId);

        LOGGER.info(String.format("Account %s added with id %s", account.getName(), account.getId()));

        final int transactionsNumber = ThreadLocalRandom.current().nextInt(0, 200);
        final List<Transaction> transactions = new ArrayList<Transaction>(transactionsNumber);

        Double total = 0.0;
        for (int i = 0; i < transactionsNumber; i++) {
            final String description = String.format("Transaction %d", i);
            final Double amount = ThreadLocalRandom.current().nextDouble(-100.0, 100.0);
            final Transaction transaction = new Transaction(description, "Personal", "Misc", System.currentTimeMillis(), amount, "single,sample");
            total += transaction.getAmount();
            transactions.add(transaction);
        }
        LOGGER.info(String.format("Account %s created %d transactions with total %.02f€", account.getId(), transactionsNumber, total));

        account.setBalance(total);

        for (final Transaction transaction : transactions) {
            addTransaction(transaction, account.getId());
        }

        final Account resultAccount = getAccountInformation(account.getId());
        LOGGER.info(String.format("Account %s balance %f ", resultAccount.getId(), (resultAccount.getStartBalance() + resultAccount.getBalance())));

        Assert.assertEquals(account.getStartBalance(), resultAccount.getStartBalance(), String.format("Account %s invalid start balance!", account.getId()));
        Assert.assertEquals(account.getBalance(), resultAccount.getBalance(), String.format("Account %s invalid balance!", account.getId()));
    }


    private String addAccount(final Account account) throws Exception {

        final String resource = String.format("http://%s:%s/accounts/", GlobalSettings.HOSTNAME, GlobalSettings.PORT);
        HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey)
                .body(account)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        final JsonNode data = response.getBody();
        final String accountId = data.getObject().get("id").toString();
        Assert.assertTrue(isUUID(accountId), "Invalid Account ID!");

        return accountId;
    }

    private String addTransaction(final Transaction transaction, final String accountId) throws Exception {
        final String resource = String.format("http://%s:%s/accounts/%s/transactions", GlobalSettings.HOSTNAME, GlobalSettings.PORT, accountId);
        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", this.apiKey)
                .body(transaction)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final JsonNode data = response.getBody();
        final String transactionId = data.getObject().get("id").toString();
        Assert.assertTrue(isUUID(transactionId), "Invalid Transaction ID!");
        LOGGER.info(String.format("Transaction %s added to account %s", transactionId, accountId));

        return transactionId;
    }

    private Account getAccountInformation(final String accountId) throws Exception {
        final String resource = String.format("http://%s:%s/accounts/%s", GlobalSettings.HOSTNAME, GlobalSettings.PORT, accountId);
        final HttpResponse<Account> response = Unirest.get(resource)
                .header("authkey", this.apiKey)
                .asObject(Account.class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        return response.getBody();
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
