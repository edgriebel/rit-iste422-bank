import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Account implements Persistable {
    public static Logger logger = LogManager.getLogger(Account.class.getName());
    public static Logger timeLogger = LogManager.getLogger("timer." + Account.class.getName());
    protected static Register SharedRegister = null;

    private Long id;
    private double balance;
    protected String name;
    private Long ownerId;
    protected Register register;
    protected double minimumBalance;
    protected double belowMinimumFee;

    // This is kind of a hack to get around having to add Register
    // to every call to new Account()
    public static void useSharedRegister(Register register) {
        logger.info("Using new shared register");
        SharedRegister = register;
    }

    public static void useIndividualRegisters() {
        if (SharedRegister != null) {
            logger.info("Removing shared register");
            SharedRegister = null;
        }
    }

    public Account() {
    	this("", -1, 0.0, -1);
    }


    public Account(String name, long id, double balance, long ownerId) {
        timeLogger.info("start _init");
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.ownerId = ownerId;
        this.register = (SharedRegister != null) ? SharedRegister : new Register();
        register.add(id, "OPEN", balance, new Date());
        timeLogger.info("end _init");
    }

    public void deposit(double amount) throws Exception {
        deposit(amount, "DEP");
    }

    public void deposit(double amount, String registerEntry) {
        timeLogger.info("start deposit");
        logger.info("account_name={} operation={} amount={}", name, "deposit", amount);
        logger.debug(name + " Balance before deposit: " + balance);
        balance += amount;
        logger.debug(name + " Balance after deposit: " + balance);
        register.add(id, registerEntry, amount, new Date());
        timeLogger.info("end deposit");
    }

    public void withdraw(double amount) throws Exception {
        withdraw(amount, "W/D");
    }

    public void withdraw(double amount, String registerEntry) {
        withdraw(amount, registerEntry, new Date());
    }
    public void withdraw(double amount, String registerEntry, Date txnDate) {
        timeLogger.info("start withdraw");
        logger.info("account_name={} operation={} amount={}", name, "withdraw", amount); //name + " Depositing " + amount);
    	logger.debug(name + " Before w/d "+ getBalance());
        balance = balance - amount;
        logger.debug(name + " After w/d " + getBalance());
        register.add(id, registerEntry, -1d * amount, txnDate);
        timeLogger.info("end withdraw");
    }

    public String getName() {
    	return name;
    }

    public Register getRegister() {
        return register;
    }

    public List<RegisterEntry> getRegisterEntries() {
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

    public Statement generateStatement() {
        timeLogger.info("start generateStatement");
        logger.info("account_name={} operation={} amount={}", name, "month_end", "");
        monthEnd();

        List<String> rtn = new ArrayList<>();
        List<RegisterEntry> registerEntries = register.getEntries();
        for (RegisterEntry entry : registerEntries) {
            String val = String.format("%-8s: %,.2f on %s", entry.entryName(), entry.amount(), entry.date().toString());
            rtn.add(val);
        }
        timeLogger.info("end generateStatement");
        logger.info("Account {}: {} register entries", name, rtn.size());
        return new Statement(name, balance, rtn);
    }

    public String toString() {
        return String.format("Account:\t'%s'\tid: %d\townerId: %d\tBalance: %s", name, id, ownerId, balance);
    }

    abstract public void monthEnd();

    @Override
    public String toCSV() throws SerializationException {
        throw new UnsupportedOperationException("METHOD NOT IMPLEMENTED");
    }

    public String [] columns()
    {
        throw new UnsupportedOperationException("METHOD NOT IMPLEMENTED");
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
