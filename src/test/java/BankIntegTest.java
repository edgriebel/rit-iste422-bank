import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class BankIntegTest {


    /**
     * Load some records, do some calculations and transactions
     */

    Bank bank;

    // Maps account ID to Owner ID
    private Map<Long, Long> accountsAndOwners;
    private long accountIds[] = {100, 200};

    /**
     * This class loads up some CSV files and runs tests on them.
     * These files are "from our production system", and the
     * tests are very specific to the data in the files, so there
     * are a lot of hardcoded entries.
     * These hardcoded values should not be changed; failures that
     * occur are most likely due to bugs introduced in the code or
     * changes to the underlying CSV files.
     *
     * Some of the things we test:
     * - Validating the initial state of the files
     * - Correct balances exist after we perform some transactions
     */


    @Before
    public void loadData() throws SerializationException, IOException {
        bank = new Bank();
        Persister.setPersisterPropertiesFile("persister_integ.properties");
        bank.loadAllRecords();
        bank.validateAccounts();
        for (long id : accountIds) {
            assertNotNull("Account with id " + id + " not found!", bank.getAccount(id));
        }
        // store account IDs with a link to owner so we don't have to enumerate them in later tests
    }

    /** because the Bank object forces Account to use a shared Account register
     * we need to manually clear it so it doesn't interfere with other tests.
     */
    @After
    public void resetState() {
        Account.useIndividualRegisters();
        Persister.resetPersistedFileNameAndDir();
    }


    private static boolean findARegex(final String target, final String [] regexes) {
        for (String regex : regexes) {
            if (target.matches(regex)) {
                return true;
            }
        }
        return false;
    }
    private Collection<RegisterEntry> excludeNonTransactions(Collection<RegisterEntry> entries) {
        String [] excludes = {".*END CHECK.*"};

        return entries.stream().filter(e -> findARegex(e.entryName(), excludes)).toList();
    }

    @Test
    public void verifyRegisterEntriesSum() throws Exception{
        for (long acctId : accountIds) {
            Collection<RegisterEntry> register = bank.getRegisterEntriesForAccount(acctId);
            double sum = 0;
            for (RegisterEntry re : register) {
                if (! re.entryName().contains("END CHECK"))
                    sum += re.amount();
            }
            assertThat(sum, is(bank.getAccount(acctId).getBalance()));
        }
    }

    @Test
    public void verifyRegisterEntriesByMonth() throws Exception{
        for (long acctId : accountIds) {
            Collection<RegisterEntry> register = bank.getRegisterEntriesForAccount(acctId);
            double sum = 0;
            for (RegisterEntry re : excludeNonTransactions(register)) {
                sum += re.amount();
            }
            assertThat(sum, is(bank.getAccount(acctId).getBalance()));
        }
    }

//    @Test
//    public void
}
