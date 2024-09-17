import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Runner {
    static {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s %n");
        System.setProperty("java.util.logging.ConsoleHandler.level", "FINE");
    }

    public static void main(String[] args) {
        Logger logger = LogManager.getLogger(Runner.class);
        System.out.println("Args: " + java.util.Arrays.asList(args));
        Owner owner = new Owner("Jane Smith");
        Random random = new Random();

//        LogMana1           logger.setLevel(Level.FINE);
        System.out.println("Creating accounts....");
        SavingsAccount savings = new SavingsAccount(
                "Savings", random.nextInt(1000), 1000.0, 0.12, owner.id());
        CheckingAccount checking = new CheckingAccount(
                "Checking", random.nextInt(1000), 100.0, random.nextInt(500), owner.id());

        System.out.println("Accounts:");
        System.out.println(savings);
        System.out.println(checking);

        try {
            savings.deposit(100);
            checking.withdraw(100);
        }
        catch (Exception e) {
            logger.warn("Exception!", e);
        }
        savings.monthEnd();
        checking.monthEnd();

        System.out.println(savings.generateStatement());
        System.out.println(checking.generateStatement());

        System.out.println();

    }
}
