import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Bank {
    public static Logger logger = LogManager.getLogger(Bank.class);
    protected Map<Long, Owner> owners = new TreeMap<>();
    protected Map<Long, Account> accounts = new TreeMap<>();

    public Account getAccount(long id) {
        return accounts.get(id);
    }

    public long putAccount(Account account) throws DuplicateKeyException, MissingRecordException {
        if (accounts.containsKey(account.getId())) {
            throw new DuplicateKeyException("Id " + account.getId() + " already exists:" + account);
        }
        if (!owners.containsKey(account.getOwnerId())) {
            throw new MissingRecordException("Owner " + account.getOwnerId() + " not found in Owners list:" + account);
        }
        accounts.put(account.getId(), account);
        return account.getId();
    }

    public Owner getOwner(long id) {
        return owners.get(id);
    }

    public long putOwner(Owner owner) throws DuplicateKeyException {
        if (owners.containsKey(owner.getId())) {
            throw new DuplicateKeyException("Id " + owner.getId() + " already exists:" + owner);
        }
        owners.put(owner.getId(), owner);
        return owner.getId();
    }

    public Collection<Statement> runMonthEnd() {
        List<Statement> statements = new ArrayList<>();
        for (Account a: accounts.values()) {
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
            accounts.put(rec.getId(), rec);
        }
        for (CheckingAccount rec : Persister.readCheckingAccountsFromCsv(checkingAccountCsvFile)) {
            accounts.put(rec.getId(), rec);
        }
        logger.info("Loaded {} Accounts", accounts.size());
        return owners.size() + accounts.size();
    }

    public int saveAllRecords(String ownersCsvFile, String checkingAccountCsvFile, String savingsAccountCsvFile) throws IOException, SerializationException {
        int ownerCount = Persister.writeRecordsToCsv(owners.values(), ownersCsvFile);
        // this splits all accounts into
        Map<Class<? extends Account>, List<Account>> splitAccounts = accounts.values().stream().collect(Collectors.groupingBy(rec -> rec.getClass()));
        int savingsCount = Persister.writeRecordsToCsv(splitAccounts.get(SavingsAccount.class), savingsAccountCsvFile);
        int checkingCount = Persister.writeRecordsToCsv(splitAccounts.get(CheckingAccount.class), checkingAccountCsvFile);
        return ownerCount + savingsCount + checkingCount;
    }

    public void clearAllRecords() {
        owners.clear();
        accounts.clear();
    }

    public void validateAccounts() {
        // Check that OwnerId's are valid
        for (Account a : accounts.values()) {
            assert owners.containsKey(a.getOwnerId()): "Account " + a + " has invalid Owner ID!";
        }
        // check that all map keys match internal IDs
        for (Map<Long, ? extends Persistable> persistableMap : List.of(owners, accounts)) {
            for (var kv : persistableMap.entrySet()) {
                assert kv.getKey().equals(kv.getValue().getId()): "ID of key != id of value: " + kv;
            }
        }
    }

}
