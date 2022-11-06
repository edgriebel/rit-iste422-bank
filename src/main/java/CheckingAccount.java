import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheckingAccount extends Account {
    public static Logger logger = LogManager.getLogger(CheckingAccount.class.getName());

    private int checkNumber;

    public CheckingAccount() {
        logger.debug("Creating no-arg checking account");
        checkNumber = 0;
    }

    public CheckingAccount(String name, double balance, Owner owner) {
        super(name, balance, owner);
        logger.debug(String.format("Creating checking account for %s: %s, %f",
               owner, name, balance));
        checkNumber = 0;
    }

    /**
     * write a check
     * @param name
     * @param amount
     * @return check number
     * @throws Exception if negative balance
     */
    public int writeCheck(String name, double amount) throws Exception {
    	logger.debug("Balance before check:" + getBalance() + " check amount: " + amount);
        withdraw(amount, String.format("Check %d", checkNumber));
    	logger.debug("Balance after check:" + getBalance());

        return checkNumber++;
    }

    @Override
    public void monthEnd() {
        if (getBalance() < getMinimumBalance()) {
            withdraw(getBelowMinimumFee(), "MINIMUM BALANCE CHARGE");
        }
        logger.info("Check # at end of month: " + checkNumber);
        register.add("END CHECK", (double)checkNumber);
    }

    public String toString() {
        return "Checking Account " + super.toString() + " Current Check #" + checkNumber;
    }
}
