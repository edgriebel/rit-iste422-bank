import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Account {
    public static Logger logger = LogManager.getLogger(Account.class.getName());
    public static Logger timeLogger = LogManager.getLogger("timer." + Account.class.getName());

    private double balance;
    protected String name;
    protected Owner owner;
    protected Register register;
    protected double minimumBalance;
    protected double belowMinimumFee;

    public Account() {
    	this("", 0.0, new Owner("NO OWNER"));
    }

    public Account(String name, double balance, Owner owner) {
        timeLogger.info("start _init");
        this.name = name;
        this.balance = balance;
        this.owner = owner;
        register = new Register();
        register.add("OPEN", balance);
        timeLogger.info("end _init");
    }

    public void deposit(double amount) throws Exception {
        deposit(amount, "DEP");
    }

    public void deposit(double amount, String registerEntry) {
        timeLogger.info("start deposit");
        logger.info(name + " Depositing " + amount);
        logger.debug(name + " Balance before deposit: " + balance);
        balance += amount;
        logger.debug(name + " Balance after deposit: " + balance);
        register.add(registerEntry, amount);
        timeLogger.info("end deposit");
    }

    public void withdraw(double amount) throws Exception {
        withdraw(amount, "W/D");
    }

    public void withdraw(double amount, String registerEntry) {
        timeLogger.info("start withdraw");
    	logger.debug(name + " Before w/d "+ getBalance());
        balance = balance - amount;
        logger.debug(name + " After w/d " + getBalance());
        register.add(registerEntry, -1d * amount);
        timeLogger.info("end withdraw");
    }

    public String getName() {
    	return name;
    }

    public Register getRegister() {
        return register;
    }

    public List<Map.Entry<String, Double>> getRegisterEntries() {
        return register.getEntries();
    }

    public double getBalance() {
        return balance;
    }

    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public void setBelowMinimumFee(double belowMinimumFee) {
        this.belowMinimumFee = belowMinimumFee;
    }

    public double getBelowMinimumFee() {
        return belowMinimumFee;
    }

    public List<String> generateStatement() {
        timeLogger.info("start generateStatement");
        monthEnd();

        List<String> rtn = new ArrayList<>();
        List<Map.Entry<String, Double>> registerEntries = register.getEntries();
        for (Map.Entry<String, Double> entry : registerEntries) {
            String val = String.format("%8s: %f", entry.getKey(), entry.getValue());
            rtn.add(val);
        }
        timeLogger.info("end generateStatement");
        return rtn;
    }

    public String toString() {
        return String.format("%s\tAccount name: %s\tBalance: %s", owner, name, balance);
    }

    abstract public void monthEnd();
}
