import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Bank {
    public static Logger logger = LogManager.getLogger(Bank.class);
    private Map<Long, Owner> owners = new TreeMap<>();
    private Map<Long, Account> accounts = new TreeMap<>();
    private Register register = new Register();

    public Bank() {
        // All accounts share a single unified register so we only
        // persist one object instead of one per account
        Account.useSharedRegister(register);
    }

    public Account getAccount(long id) {
        return accounts.get(id);
    }

    public Collection<Account> getAllAccounts() {
        return Collections.unmodifiableCollection(accounts.values());
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

    public Collection<Owner> getAllOwners() {
        return Collections.unmodifiableCollection(owners.values());
    }

    public long putOwner(Owner owner) throws DuplicateKeyException {
        if (owners.containsKey(owner.getId())) {
            throw new DuplicateKeyException("Id " + owner.getId() + " already exists:" + owner);
        }
        owners.put(owner.getId(), owner);
        return owner.getId();
    }

    public Collection<RegisterEntry> getAllRegisterEntries() {
        return Collections.unmodifiableCollection(register.getEntries());
    }

    public Collection<RegisterEntry> getRegisterEntriesForAccount(long accountId) {
        return Collections.unmodifiableCollection(register.getEntriesForAccount(accountId));
    }
    public Collection<Statement> runMonthEnd() {
        List<Statement> statements = new ArrayList<>();
        for (Account a: accounts.values()) {
            a.monthEnd();
            statements.add(a.generateStatement());
        }
        return statements;
    }

    public int loadAllRecords() throws IOException, SerializationException {
        clearAllRecords();
        for (Owner o : Persister.readOwnersFromCsv()) {
            owners.put(o.id(), o);
        }
        logger.info("Loaded {} Owners", owners.size());
        for (SavingsAccount rec : Persister.readSavingsAccountsFromCsv()) {
            accounts.put(rec.getId(), rec);
        }
        for (CheckingAccount rec : Persister.readCheckingAccountsFromCsv()) {
            accounts.put(rec.getId(), rec);
        }
        // we need to clear the register because inserting accounts above creates entries
        register.clear();
        for (RegisterEntry rec : Persister.readRegisterEntriesFromCsv()) {
            register.addRegisterEntry(rec);
        }
        logger.info("Loaded {} Accounts", accounts.size());
        return owners.size() + accounts.size() + register.getEntries().size();
    }

    public int saveAllRecords() throws IOException, SerializationException {
        int ownerCount = Persister.writeRecordsToCsv(owners.values(), "owners");
        // this splits all accounts into
        Map<Class<? extends Account>, List<Account>> splitAccounts = accounts.values().stream().collect(Collectors.groupingBy(rec -> rec.getClass()));
        int savingsCount = Persister.writeRecordsToCsv(splitAccounts.get(SavingsAccount.class), "savings");
        int checkingCount = Persister.writeRecordsToCsv(splitAccounts.get(CheckingAccount.class),"checking");
        int registerCount = Persister.writeRecordsToCsv(register.getEntries(),"register");
        return ownerCount + savingsCount + checkingCount + registerCount;
    }

    public void clearAllRecords() {
        owners.clear();
        accounts.clear();
        register.clear();
    }

    public void validateAccounts() {
        // Check that OwnerId's are valid
        for (Account a : accounts.values()) {
            assert owners.containsKey(a.getOwnerId()): "Account " + a + " has an unknown Owner ID!";
        }

        // check that every object's id matches the key it's stored under
        for (Map<Long, ? extends Persistable> persistableMap : List.of(owners, accounts)) {
            for (var kv : persistableMap.entrySet()) {
                assert kv.getKey().equals(kv.getValue().getId()): "ID of key != id of value: " + kv;
            }
        }
    }

}
