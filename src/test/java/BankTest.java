import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BankTest {

    @Before
    public void loadAndSaveToTemporaryDir() throws IOException {
        Persister.setPersisterPropertiesFile("persister.properties");
    }

    /** because the Bank object forces Account to use a shared Account register
     * we need to manually clear it so it doesn't interfere with other tests.
     */
    @After
    public void resetState() {
        Account.useIndividualRegisters();
        Persister.resetPersistedFileNameAndDir();
    }

    @Test
    public void givenOwnersAndAccounts_whenWriteCsv_thenReadCsvIsEqual() throws SerializationException, IOException, MissingRecordException, DuplicateKeyException {
        Random r = new Random();
        Bank bank = new Bank();
        for (int i = 0; i < 1; i++) {
            long ownerId = r.nextLong();
            bank.putOwner(new Owner("cust" + i, ownerId, new Date(), "" + i, i + " Main St", null, null, null, null));
            bank.putAccount(new SavingsAccount("acct_" + i, i+100L, i * 100, i * 0.01, ownerId));
            bank.putAccount(new CheckingAccount("acct_" + i, i+200L, i * 200, i, ownerId));
        }
        bank.validateAccounts();
        // save contents for later
        var origOwners = bank.getAllOwners();
        var origAccounts = bank.getAllAccounts();
        var origRegisterEntries = bank.getAllRegisterEntries();
        int savedCount = bank.saveAllRecords();
        int loadedCount = bank.loadAllRecords();
        assertThat("Records saved should be the same as records loaded", savedCount, is(loadedCount));
        bank.validateAccounts();
        assertThat(bank.getAllOwners(), hasItems(origOwners.toArray(new Owner[0])));
        assertThat(bank.getAllAccounts(), hasItems(origAccounts.toArray(new Account[0])));
        assertThat(bank.getAllRegisterEntries(), hasItems(origRegisterEntries.toArray(new RegisterEntry[0])));
    }

    @Test
    public void givenEmptyOwnersAndAccounts_whenWriteAndReadCsv_thenMapsShouldBeEmpty() throws SerializationException, IOException {
        Bank bank = new Bank();
        bank.validateAccounts();
        int savedCount = bank.saveAllRecords();
        assertThat(savedCount, is(0));
        int loadCount = bank.loadAllRecords();
        assertThat(loadCount, is(0));
        bank.validateAccounts();
        assertTrue("There should be no Owners loaded", bank.getAllOwners().isEmpty());
        assertTrue("There should be no Accounts loaded", bank.getAllAccounts().isEmpty());
        assertTrue("There should be no register entries", bank.getAllRegisterEntries().isEmpty());
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
