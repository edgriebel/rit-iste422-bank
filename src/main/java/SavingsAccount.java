import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SavingsAccount extends Account {
    public static Logger logger = LogManager.getLogger(SavingsAccount.class);

    /** interestRate is an annualized fractional value, e.g. 1% interest is 0.01 */
    double interestRate;

    public SavingsAccount() {
        logger.debug("Created no-arg savings account");
        interestRate = 0.0;
    }

    public SavingsAccount(String name, double balance, double interestRate, Owner owner) {
        super(name, balance, owner);
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest rate must be >= 0");
        }
        this.interestRate = interestRate;
        logger.debug("Created savings account:" + this);
    }

    public double getInterestRate() {
    	return interestRate;
    }
    
    @Override
    public void monthEnd() {
        if (getBalance() < getMinimumBalance()) {
            withdraw(getBelowMinimumFee(), "MINIMUM BALANCE CHARGE");
        }
        double interest = Math.round(interestRate * getBalance() / 12d * 100d) / 100d;
        if (interest > 0d) {
            deposit(interest, "INTEREST");
        }
        // Question: what else do we need to do here??
    }

    public String toString() {
        return "Savings Account " + super.toString() +
                " Interest Rate: " + (100*interestRate) + "%";
    }
}
