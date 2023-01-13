import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public class BankRunner {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s %n");
        System.setProperty("java.util.logging.ConsoleHandler.level", "FINE");
    }

    public static void main(String[] args) throws DuplicateKeyException, MissingRecordException, SerializationException, IOException {
        Logger logger = LogManager.getLogger(BankRunner.class);
        if (args.length > 0) {
            System.out.println("Command-line arguments passed in: " + Arrays.asList(args));
        }
        Random random = new Random();

        Bank bank = new Bank();
        long ownerId = bank.putOwner(new Owner("Jane Smith"));

        System.out.println("Creating accounts....");
        long savingsId = bank.putAccount(new SavingsAccount(
                "Savings", random.nextInt(1000), 1000.0, 0.12, ownerId));
        long checkingId = bank.putAccount( new CheckingAccount(
                "Checking", random.nextInt(1000), 100.0, random.nextInt(500), ownerId));
        bank.validateAccounts();
        System.out.println("Accounts:");
        System.out.println(bank.getAccount(savingsId));
        System.out.println(bank.getAccount(checkingId));

        try {
            bank.getAccount(savingsId).deposit(100);
            bank.getAccount(checkingId).withdraw(100);
        }
        catch (Exception e) {
            logger.warn("Exception!", e);
        }
        Collection<Statement> statements = bank.runMonthEnd();
        for (Statement s : statements) {
            System.out.println(s);
        }
        bank.saveAllRecords("owners.csv", "checking.csv", "savings.csv");
    }
}