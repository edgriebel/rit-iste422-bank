import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BankTest {

    @Test
    public void givenOwnersAndAccounts_whenWriteCsv_thenReadCsvIsEqual() throws SerializationException, IOException {
        Random r = new Random();
        Bank bank = new Bank();
        for (int i = 0; i < 1; i++) {
            long ownerId = r.nextLong();
            bank.owners.put(ownerId, new Owner("cust" + i, ownerId, new Date(), "" + i, i + " Main St", null, null, null, null));
            bank.savingsAccounts.put(i+100L, new SavingsAccount("acct_" + i, i+100L, i * 100, i * 0.01, ownerId));
            bank.checkingAccounts.put(i+200L, new CheckingAccount("acct_" + i, i+200L, i * 200, i, ownerId));
        }
        bank.validateAccounts();
        // save contents for later
        var origOwners = bank.owners.values();
        var origChecking = bank.checkingAccounts.values();
        var origSavings = bank.savingsAccounts.values();
        String TEMP = System.getProperty("java.io.tmpdir") + File.separator;
        int savedCount = bank.saveAllRecords(TEMP + "owners.csv", TEMP + "checking.csv", TEMP + "savings.csv");
        int loadedCount = bank.loadAllRecords(TEMP + "owners.csv", TEMP + "checking.csv", TEMP + "savings.csv");
        assertThat("Records saved should be the same as records loaded", savedCount, is(loadedCount));
        bank.validateAccounts();
        assertThat(bank.owners.values(), hasItems(origOwners.toArray(new Owner[0])));
        assertThat(bank.savingsAccounts.values(), hasItems(origSavings.toArray(new SavingsAccount[0])));
        assertThat(bank.checkingAccounts.values(), hasItems(origChecking.toArray(new CheckingAccount[0])));
    }

    @Test
    public void givenEmptyOwnersAndAccounts_whenWriteAndReadCsv_thenMapsShouldBeEmpty() throws SerializationException, IOException {
        Random r = new Random();
        Bank bank = new Bank();
        bank.validateAccounts();
        String TEMP = System.getProperty("java.io.tmpdir") + File.separator;
        int savedCount = bank.saveAllRecords(TEMP + "owners.csv", TEMP + "checking.csv", TEMP + "savings.csv");
        assertThat(savedCount, is(0));
        int loadCount = bank.loadAllRecords(TEMP + "owners.csv", TEMP + "cheking.csv", TEMP + "savings.csv");
        assertThat(loadCount, is(0));
        bank.validateAccounts();
        assertTrue("There should be no Owners loaded", bank.owners.isEmpty());
        assertTrue("There should be no SavingsAccounts loaded", bank.savingsAccounts.isEmpty());
        assertTrue("There should be no CheckingAccounts loaded", bank.checkingAccounts.isEmpty());
    }

    @Test
    public void givenOwners_whenMapKeyMismatch_thenError() {
        // TODO complete..
    }

    @Test
    public void givenOwners_whenMapKeysMatch_thenNoErrors() {
        // TODO complete..
    }

    @Test
    public void givenSavingsAccounts_whenMapKeyMismatch_thenError() {
        // TODO complete..
    }

    @Test
    public void givenSavingsAccounts_whenMapKeysMatch_thenNoErrors() {
        // TODO complete..
    }

    @Test
    public void givenCheckingAccounts_whenMapKeyMismatch_thenError() {
        // TODO complete..
    }
    @Test
    public void givenCheckingAccounts_whenMapKeysMatch_thenNoErrors() {
        // TODO complete..
    }
}
