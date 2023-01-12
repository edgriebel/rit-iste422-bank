import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Account implements Persistable {
    public static Logger logger = LogManager.getLogger(Account.class.getName());
    public static Logger timeLogger = LogManager.getLogger("timer." + Account.class.getName());

    private Long id;
    private double balance;
    protected String name;
    private Long ownerId;
    protected Register register;
    protected double minimumBalance;
    protected double belowMinimumFee;

    public Account() {
    	this("", -1, 0.0, -1);
    }

    public Account(String name, long id, double balance, long ownerId) {
        timeLogger.info("start _init");
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.ownerId = ownerId;
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
        return String.format("%s\tAccount id: %d ownerId: %d\tBalance: %s", name, id, ownerId, balance);
    }

    abstract public void monthEnd();

    @Override
    public String toCSV() throws SerializationException {
        throw new UnsupportedOperationException();
    }

    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Double.compare(account.balance, balance) == 0 && Double.compare(account.minimumBalance, minimumBalance) == 0 && Double.compare(account.belowMinimumFee, belowMinimumFee) == 0 && id.equals(account.id) && name.equals(account.name) && ownerId.equals(account.ownerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance, name, ownerId, minimumBalance, belowMinimumFee);
    }
}
