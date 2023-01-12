import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Bank {
    public static Logger logger = LogManager.getLogger(Bank.class);
    protected Map<Long, Owner> owners = new TreeMap<>();
    protected Map<Long, SavingsAccount> savingsAccounts = new TreeMap<>();
    protected Map<Long, CheckingAccount> checkingAccounts = new TreeMap<>();

    public SavingsAccount getSavingsAccount(long id) {
        return savingsAccounts.get(id);
    }

    public void putSavingsAccount(SavingsAccount account) throws DuplicateRecordException, MissingRecordException {
        if (savingsAccounts.containsKey(account.getId())) {
            throw new DuplicateRecordException("Id " + account.getId() + " already exists:" + account);
        }
        if (!owners.containsKey(account.getOwnerId())) {
            throw new MissingRecordException("Owner " + account.getOwnerId() + " not found in Owners list:" + account);
        }
        savingsAccounts.put(account.getId(), account);
    }

    public CheckingAccount getCheckingAccount(long id) {
        return checkingAccounts.get(id);
    }

    public void putCheckingAccount(CheckingAccount account) throws DuplicateRecordException, MissingRecordException {
        if (checkingAccounts.containsKey(account.getId())) {
            throw new DuplicateRecordException("Id " + account.getId() + " already exists:" + account);
        }
        if (!owners.containsKey(account.getOwnerId())) {
            throw new MissingRecordException("Owner " + account.getOwnerId() + " not found in Owners list:" + account);
        }
        checkingAccounts.put(account.getId(), account);
    }

    public Owner getOwner(long id) {
        return owners.get(id);
    }

    public void putOwner(Owner owner) throws DuplicateRecordException {
        if (owners.containsKey(owner.getId())) {
            throw new DuplicateRecordException("Id " + owner.getId() + " already exists:" + owner);
        }
        owners.put(owner.getId(), owner);
    }

    public Collection<Object> runMonthEnd() {
        List<Object> statements = new ArrayList<>();
        List<Account> accounts = new ArrayList<>(savingsAccounts.values());
        accounts.addAll(checkingAccounts.values());
        for (Account a: accounts) {
            a.monthEnd();
            statements.add(a.generateStatement());
        }
        return statements;
    }

    public int loadAllRecords(String ownersCsvFile, String checkingAccountCsvFile, String savingsAccountCsvFile) throws IOException, SerializationException {
        clearAllRecords();
        for (Owner o : Persister.readOwnersFromCsv(ownersCsvFile)) {
            owners.put(o.id(), o);
        }
        logger.info("Loaded {} Owners", owners.size());
        for (SavingsAccount rec : Persister.readSavingsAccountsFromCsv(savingsAccountCsvFile)) {
            savingsAccounts.put(rec.getId(), rec);
        }
        logger.info("Loaded {} Savings Accounts", savingsAccounts.size());
        for (CheckingAccount rec : Persister.readCheckingAccountsFromCsv(checkingAccountCsvFile)) {
            checkingAccounts.put(rec.getId(), rec);
        }
        logger.info("Loaded {} Checkng Accounts", checkingAccounts.size());
        return owners.size() + savingsAccounts.size() + checkingAccounts.size();
    }

    public int saveAllRecords(String ownersCsvFile, String checkingAccountCsvFile, String savingsAccountCsvFile) throws IOException, SerializationException {
        int ownerCount = Persister.writeRecordsToCsv(owners.values(), ownersCsvFile);
        int savingsCount = Persister.writeRecordsToCsv(savingsAccounts.values(), savingsAccountCsvFile);
        int checkingCount = Persister.writeRecordsToCsv(checkingAccounts.values(), checkingAccountCsvFile);
        return ownerCount + savingsCount + checkingCount;
    }

    public void clearAllRecords() {
        owners.clear();
        savingsAccounts.clear();
        checkingAccounts.clear();
    }

    public void validateAccounts() {
        // Check that OwnerId's are valid
        for (Account a : savingsAccounts.values()) {
            assert owners.containsKey(a.getOwnerId()): "Account " + a + " has invalid Owner ID!";
        }
        for (Account a : checkingAccounts.values()) {
            assert owners.containsKey(a.getOwnerId()): "Account " + a + " has invalid Owner ID!";
        }
        // check that all map keys match internal IDs
        for (Map<Long, ? extends Persistable> persistableMap : List.of(owners, savingsAccounts, checkingAccounts, owners)) {
            for (var kv : persistableMap.entrySet()) {
                assert kv.getKey().equals(kv.getValue().getId()): "ID of key != id of value: " + kv;
            }
        }
    }

}
