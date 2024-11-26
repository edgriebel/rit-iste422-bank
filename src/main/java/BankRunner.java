import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class BankRunner {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s %n");
        System.setProperty("java.util.logging.ConsoleHandler.level", "FINE");
    }

    public static void main(String[] args) throws DuplicateKeyException, MissingRecordException, SerializationException, IOException, InterruptedException {
        Logger logger = LogManager.getLogger(BankRunner.class);
        if (args.length > 0) {
            System.out.println("Command-line arguments passed in: " + Arrays.asList(args));
        }
        int count = 1;
        int delay = 1000; // msec
        // process cmdline arguments
        if (args.length > 0 && args[0].equalsIgnoreCase("--loop")) {
            count = -1;
            if (args.length > 1) {
                delay = Integer.parseInt(args[1]);
            }
        }
        count = -1;
        delay = 10;
        Random random = new Random();
	    List<Bank> banks = new ArrayList<>();

        do {
            Bank bank = new Bank();
            if (count % 5 == 0) {
	    	// save every 5 to illustrate a memory leak
	    	 banks.add(bank);
	    }
            long ownerId = bank.putOwner(new Owner("Jane Smith"));

            System.out.println("Creating accounts....");
            long savingsId = bank.putAccount(new SavingsAccount(
                    "Savings", random.nextInt(100000), 1000.0, 0.12, ownerId));
            long checkingId = bank.putAccount(new CheckingAccount(
                    "Checking", random.nextInt(100000), 100.0, random.nextInt(500), ownerId));
            bank.validateAccounts();
            System.out.println("Accounts:");
            System.out.println(bank.getAccount(savingsId));
            System.out.println(bank.getAccount(checkingId));

            try {
                bank.getAccount(savingsId).deposit(100);
                bank.getAccount(checkingId).withdraw(100);
            } catch (Exception e) {
                logger.warn("Exception!", e);
            }
            Collection<Statement> statements = bank.runMonthEnd();
            for (Statement s : statements) {
                System.out.println(s);
            }
            for (RegisterEntry re : bank.getAllRegisterEntries()) {
                System.out.println(re);
            }
            bank.saveAllRecords();
            count--;
            if (count != 0) {
                System.out.println("Iterations left: " + count);
                System.out.printf("Iteration = %d, sleeping for %d...%n", count+1, delay);
                Thread.sleep(delay);
            }
        } while (count != 0);
    }
}
