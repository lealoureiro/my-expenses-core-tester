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
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Leandro Loureiro
 */
public class ConcurrentTest {

    private static final Logger LOGGER = LogManager.getLogger(ConcurrentTest.class);

    @Test(threadPoolSize = 4, invocationCount = 4)
    public void concurrentUsageScenario() throws Exception {

        final long userId = Thread.currentThread().getId();
        final String accountName = String.format("Account %010d", Math.abs(ThreadLocalRandom.current().nextInt()));
        final Long startBalance = ThreadLocalRandom.current().nextLong(0, 100000);
        final Account account = new Account(accountName, "current account", startBalance, "EUR");
        final double amount = account.getStartBalance() / 100d;
        LOGGER.info(String.format("User %d created account %s with start balance %.02f", userId, accountName, amount));

        final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);
        final String key = getAccessKey(credentials);

        final String accountId = addAccount(account, key);
        account.setId(accountId);

        LOGGER.info(String.format("Account %s added with id %s", account.getName(), account.getId()));

        final int transactionsNumber = ThreadLocalRandom.current().nextInt(0, 100);
        final List<Transaction> transactions = new ArrayList<>(transactionsNumber);

        final Long currentTimestamp = new Date().getTime();
        final Long firstDay2012 = 1325372400000L;
        final Long timeInterval = currentTimestamp - firstDay2012;

        final long numberTransactions = ThreadLocalRandom.current().nextLong(0, 1000);
        Long total = 0L;
        for (int i = 0; i < numberTransactions; i++) {
            final String description = String.format("Transaction %d", i);
            final Long transactionAmount = ThreadLocalRandom.current().nextLong(-10000, 10000);
            final Long timestamp = firstDay2012 + ThreadLocalRandom.current().nextLong(0, timeInterval);
            final Transaction transaction = new Transaction(description, "Personal", "Misc", timestamp, transactionAmount);
            transaction.getTags().add("sample tag");
            total += transaction.getAmount();
            transactions.add(transaction);
        }

        final double totalAmount = total / 100d;
        LOGGER.info(String.format("Account %s created %d transactions with total %.02f", account.getId(), transactionsNumber, totalAmount));

        account.setBalance(total);

        for (final Transaction transaction : transactions) {
            addTransaction(transaction, account.getId(), key);
        }

        final Account resultAccount = getAccountInformation(account.getId(), key);
        total += resultAccount.getStartBalance();
        final double resultAmount = total / 100d;
        LOGGER.info(String.format("Account %s balance %.02f", resultAccount.getId(), resultAmount));

        Assert.assertEquals(account.getStartBalance(), resultAccount.getStartBalance(), String.format("Account %s invalid start balance!", account.getId()));
        Assert.assertEquals(account.getBalance(), resultAccount.getBalance(), String.format("Account %s invalid balance!", account.getId()));
    }

    private String getAccessKey(final Credentials credentials) throws Exception {
        LOGGER.info("Getting access key to proceed with test");

        final String resource = String.format("%s/keys/", GlobalSettings.SERVER);
        final HttpResponse<KeyData> response = Unirest.post(resource)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .body(credentials)
                .asObject(KeyData.class);

        final KeyData newKeyData = response.getBody();
        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP status code!");
        return newKeyData.getKey();
    }

    private String addAccount(final Account account, final String key) throws Exception {

        final String resource = String.format("%s/accounts/", GlobalSettings.SERVER);
        HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", key)
                .body(account)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        final JsonNode data = response.getBody();
        final String accountId = data.getObject().get("id").toString();
        Assert.assertTrue(isUUID(accountId), "Invalid Account ID!");

        return accountId;
    }

    private String addTransaction(final Transaction transaction, final String accountId, final String key) throws Exception {
        final String resource = String.format("%s/accounts/%s/transactions/", GlobalSettings.SERVER, accountId);
        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", key)
                .body(transaction)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final JsonNode data = response.getBody();
        final String transactionId = data.getObject().get("id").toString();
        Assert.assertTrue(isUUID(transactionId), "Invalid Transaction ID!");
        LOGGER.info(String.format("Transaction %s added to account %s", transactionId, accountId));

        return transactionId;
    }

    private Account getAccountInformation(final String accountId, final String key) throws Exception {
        final String resource = String.format("%s/accounts/%s", GlobalSettings.SERVER, accountId);
        final HttpResponse<Account> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", key)
                .asObject(Account.class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        return response.getBody();
    }

    private static boolean isUUID(String string) {
        try {
            UUID.fromString(string);
            return true;
        } catch (final Exception ex) {
            return false;
        }
    }
}
