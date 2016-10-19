package com.myexpenses.core.test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.myexpenses.core.test.models.Account;
import com.myexpenses.core.test.models.Credentials;
import com.myexpenses.core.test.models.Transaction;
import com.myexpenses.core.test.models.TransactionTimestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.UUID;

/**
 * @author Leandro Loureiro
 */
public class TransactionsTest {

    private static final Logger LOGGER = LogManager.getLogger(TransactionsTest.class);
    private static final Random RANDOM_GENERATOR = new SecureRandom();

    private final Credentials credentials = new Credentials(GlobalSettings.TEST_USER, GlobalSettings.TEST_PASSWORD);

    private String apiKey;
    private String sampleAccountId;


    @BeforeClass
    public final void GetKey() throws Exception {
        LOGGER.info("Getting Key to start the tests...");
        apiKey = TestUtils.getNewKey(credentials).getKey();
    }


    @Test
    public final void GetSampleAccount() throws Exception {
        LOGGER.info("Getting sample account...");

        final String resource = String.format("%s/accounts/", GlobalSettings.SERVER);
        final HttpResponse<Account[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey)
                .asObject(Account[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final Account[] accounts = response.getBody();
        if (accounts.length == 0) {
            throw new SkipException("No accounts found to execute test!");
        }

        final Account account = accounts[RANDOM_GENERATOR.nextInt(accounts.length)];

        Assert.assertTrue(isUUID(account.getId()), "Invalid account ID!");
        sampleAccountId = account.getId();

        LOGGER.info(String.format("Account ID: %s", sampleAccountId));
        LOGGER.info(String.format("Account Name: %s", account.getName()));
        LOGGER.info(String.format("Account Balance: %s", account.getBalance()));
        LOGGER.info(String.format("Account Start Balance: %.02f", account.getStartBalance() / 100.0));
        LOGGER.info(String.format("Account Currency: %s", account.getCurrency()));
        LOGGER.info(String.format("Account Type: %s", account.getType()));
    }


    @Test(dependsOnMethods = "GetSampleAccount")
    public final void GetAccountTransactions() throws Exception {

        LOGGER.info(String.format("Getting transactions for account %s ...", sampleAccountId));

        final Transaction[] transactions = getAccountTransactions(sampleAccountId);

        LOGGER.info(String.format("Fetched %d transactions", transactions.length));
    }


    @Test(dependsOnMethods = "GetSampleAccount")
    public final void AddTransaction() throws Exception {
        LOGGER.info("Adding transaction test...");

        final String description = String.format("Sample Transaction %d", Math.abs(RANDOM_GENERATOR.nextInt()));
        final Long amount = RANDOM_GENERATOR.nextLong() % 10000;
        final Transaction transaction = new Transaction(description, "Personal", "Misc", System.currentTimeMillis(), amount, "single,sample");

        final String resource = String.format("%s/accounts/%s/transactions", GlobalSettings.SERVER, sampleAccountId);
        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", apiKey)
                .body(transaction)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final JsonNode data = response.getBody();
        final String transactionId = data.getObject().get("id").toString();
        Assert.assertTrue(isUUID(transactionId), "Invalid Transaction ID!");
        LOGGER.info(String.format("Transaction %s added to account %s", transactionId, sampleAccountId));
    }

    @Test
    public final void FilterTransactionsTimeRange() throws Exception {

        final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        LOGGER.info("Testing fetch transactions by range");
        LOGGER.info("Creating new account...");

        final String accountName = String.format("Sample Account %d", Math.abs(RANDOM_GENERATOR.nextInt()));
        final Long startBalance = Math.abs(RANDOM_GENERATOR.nextLong() % 100000);
        final Account account = new Account(accountName, "current Account", startBalance, "EUR");

        String resource = String.format("%s/accounts/", GlobalSettings.SERVER);
        HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", apiKey)
                .body(account)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        account.setId(response.getBody().getObject().get("id").toString());

        final String description1 = String.format("Sample Transaction %d", Math.abs(RANDOM_GENERATOR.nextInt()));
        final Long amount1 = RANDOM_GENERATOR.nextLong() % 10000;
        final Transaction transaction1 = new Transaction(description1, "Personal", "Misc", format.parse("30-05-2016").getTime(), amount1, "single,sample");

        final String description2 = String.format("Sample Transaction %d", Math.abs(RANDOM_GENERATOR.nextInt()));
        final Long amount2 = RANDOM_GENERATOR.nextLong() % 10000;
        final Transaction transaction2 = new Transaction(description2, "Personal", "Misc", format.parse("29-05-2016").getTime(), amount2, "single,sample");

        addTransactionToAccount(transaction1, account.getId());
        addTransactionToAccount(transaction2, account.getId());

        final Transaction[] transactions = getAccountTransactions(account.getId());
        Assert.assertEquals(transactions.length, 2, "Invalid number of transactions fetched!");

        resource = String.format("%s/accounts/%s/transactions/", GlobalSettings.SERVER, account.getId());
        final HttpResponse<Transaction[]> response2 = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey)
                .queryString("start", format.parse("28-05-2016").getTime())
                .queryString("end", format.parse("30-05-2016").getTime())
                .asObject(Transaction[].class);

        Assert.assertEquals(response2.getStatus(), 200, "Invalid HTTP code!");
        Assert.assertEquals(response2.getBody().length, 1, "Invalid number of transactions fetched!");
        Assert.assertEquals(response2.getBody()[0].getDescription(), transaction2.getDescription(), "Invalid transaction fetched!");
    }

    @Test(dependsOnMethods = "GetSampleAccount")
    public final void RemoveTransaction() throws Exception {
        LOGGER.info("Deleting transaction test...");

        if (sampleAccountId == null) {
            throw new SkipException("Not account found to perform test!");
        }

        final Transaction[] transactions = getAccountTransactions(sampleAccountId);
        if (transactions.length == 0) {
            throw new SkipException("No transactions found to perform test!");
        }

        final Transaction transaction = transactions[RANDOM_GENERATOR.nextInt(transactions.length)];

        final int initialTransactions = getAccountInformation(sampleAccountId).getTransactions();
        LOGGER.info(String.format("Transactions number before deletion %d", initialTransactions));

        LOGGER.info(String.format("Deleting transaction %s from account %s", transaction.getId(), sampleAccountId));
        final String resource = String.format("%s/accounts/%s/transactions/%s", GlobalSettings.SERVER, sampleAccountId, transaction.getId());
        HttpResponse<String> response = Unirest.delete(resource)
                .header("authkey", apiKey)
                .header("Content-type", "application/json")
                .body(new TransactionTimestamp(transaction.getTimestamp()))
                .asString();

        Assert.assertEquals(response.getStatus(), 204, "Invalid HTTP code!");

        final int finalTransactions = getAccountInformation(sampleAccountId).getTransactions();
        LOGGER.info(String.format("Transactions number after deletion %d", finalTransactions));
        Assert.assertEquals((initialTransactions - 1), finalTransactions, "Transaction not deleted!");

        response = Unirest.delete(resource)
                .header("authkey", apiKey)
                .header("Content-type", "application/json")
                .body(new TransactionTimestamp(transaction.getTimestamp()))
                .asString();

        Assert.assertEquals(response.getStatus(), 404, "Invalid HTTP code!");
    }

    private Account getAccountInformation(final String accountId) throws Exception {
        final String resource = String.format("%s/accounts/%s", GlobalSettings.SERVER, accountId);
        final HttpResponse<Account> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey)
                .asObject(Account.class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        return response.getBody();
    }

    private Transaction[] getAccountTransactions(final String accountId) throws Exception {

        final String resource = String.format("%s/accounts/%s/transactions/", GlobalSettings.SERVER, accountId);
        final HttpResponse<Transaction[]> response = Unirest.get(resource)
                .header("Accept", "application/json")
                .header("authkey", apiKey)
                .asObject(Transaction[].class);

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");
        return response.getBody();
    }


    private String addTransactionToAccount(final Transaction transaction, final String accountId) throws Exception {

        final String resource = String.format("%s/accounts/%s/transactions", GlobalSettings.SERVER, accountId);
        final HttpResponse<JsonNode> response = Unirest.post(resource)
                .header("Accept", "application/json")
                .header("Content-type", "application/json")
                .header("authkey", apiKey)
                .body(transaction)
                .asJson();

        Assert.assertEquals(response.getStatus(), 200, "Invalid HTTP code!");

        final JsonNode data = response.getBody();
        return data.getObject().get("id").toString();
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
