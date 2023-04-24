import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class BankIntegTest {


    /**
     * Load some records, do some calculations and transactions
     */

    Bank bank;

    // Maps account ID to Owner ID
    private Map<Long, Long> accountsAndOwners;
    private static List<Long> accountIds = new ArrayList<>();

    /**
     * This class loads up some CSV files and runs tests on them.
     * These files are from our "production system"
     *
     * Some of the things we could test:
     * - Validating the initial state of the files
     * - Validating that transactions sum up to account balance
     * - Correct balances exist after we perform a complicated series of transactions
     */

    @BeforeClass
    public static void getAccountIds() throws SerializationException, IOException {
        Persister.setPersisterPropertiesFile("persister_integ.properties");

        for (Account sa : Persister.readSavingsAccountsFromCsv()) {
            accountIds.add(sa.getId());
        }
        for (Account ca : Persister.readCheckingAccountsFromCsv()) {
            accountIds.add(ca.getId());
        }

        System.out.println("AccountIds in Accounts files: " + accountIds);
    }

    @Before
    public void loadData() throws SerializationException, IOException {
        Persister.setPersisterPropertiesFile("persister_integ.properties");
        bank = new Bank();
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
                return false;
            }
        }
        return true;
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
            for (RegisterEntry re : excludeNonTransactions(register)) {
                sum += re.amount();
            }
            assertEquals("Sum of register entries should be balance", sum, bank.getAccount(acctId).getBalance(), 0.001);
        }
    }

}
