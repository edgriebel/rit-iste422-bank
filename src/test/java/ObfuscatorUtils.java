import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class ObfuscatorUtils {

    public static <T extends Persistable> T replaceRecord(long recordId, T newRecord, List<T> records) {
        T foundRec = null;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId() == recordId) {
                foundRec = records.set(i, newRecord);
            }
        }
        return foundRec;
    }

    public static <T extends Persistable> List<T> findRecords(long recordId, Collection<T> records) {
        List<T> recordsFound = new ArrayList<>();

        for (T r : records) {
            if (r.getId() == recordId) {
                recordsFound.add(r);
            }
        }
        return recordsFound;
    }

    /*
     * To run, add this target to build.gradle and run "gradle runObfuscatorUtils":
     * 
     * task runObfuscatorUtils(type: JavaExec) { group="Execution";
     * classpath=sourceSets.test.runtimeClasspath; main="ObfuscatorUtils" }
     *
     */
    public static void main(String[] args) {
        // Set up some collections to demo these methods
        List<Owner> owners = new ArrayList<>();
        owners.add(new Owner("Owner 1", 1, new Date(), "1234567890",
                "123 Main Street", "", "Rochester", "NY", "14624"));
        owners.add(new Owner("Owner 2", 2, new Date(), "2345678901",
                "456 Main Street", "", "Rochester", "NY", "14624"));
        owners.add(new Owner("Owner 3", 3, new Date(), "3456789012",
                "789 Main Street", "", "Rochester", "NY", "14624"));

        List<Account> accounts = new ArrayList<>();
        accounts.add(new CheckingAccount("Checking 1", 1, 100, 33, 1));
        accounts.add(new SavingsAccount("Savings 2", 2, 100, 0.05, 1));
        accounts.add(new CheckingAccount("Checking 3", 3, 100, 33, 1));
        accounts.add(new CheckingAccount("Checking 4", 4, 100, 22, 2));
        accounts.add(new SavingsAccount("Savings 5", 5, 100, 0.05, 2));
        accounts.add(new CheckingAccount("Checking 6", 6, 100, 22, 3));

        // Let's find the ownerId = 2 and accountId = 2
        // note that Owner and Account are both of type Persistable so can be used in
        // these methods
        Owner owner2 = findRecords(2, owners).get(0);
        Account account2 = findRecords(2, accounts).get(0);
        System.out.println(owner2);
        System.out.println(account2);

        // Let's update the record of owner #2 including the id
        Owner newOwner = new Owner(owner2.name(), 44, owner2.dob(), owner2.ssn(), owner2.address(), owner2.address2(),
                owner2.city(),
                owner2.state(), owner2.zip());
        Owner origOwner = replaceRecord(2, newOwner, owners);
        assert origOwner.getId() == 2;

        // Because the owner id has changed, we need to update the accounts with this
        // owner
        for (Account a : accounts) {
            if (a.getOwnerId() == 2) {
                final Account newAccount;
                // note here how we create the same account type with the instanceof check
                // and temporary assignment to ca or sa
                if (a instanceof SavingsAccount sa) {
                    newAccount = new SavingsAccount(sa.getName(), sa.getId(), sa.getBalance(), sa.getInterestRate(),
                            newOwner.getId());
                } else if (a instanceof CheckingAccount ca) {
                    // Note that there needs to be a public getter for checkNumber
                    newAccount = new CheckingAccount(ca.getName(), ca.getId(), ca.getBalance(), 0, newOwner.getId());
                } else {
                    newAccount = null;
                }
                System.out.println("Updated account, old account:" + replaceRecord(a.getId(), newAccount, accounts));
            }
        }
    }
}