import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Account {
    public static Logger logger = LogManager.getLogger(Account.class.getName());

    private double balance;
    protected String name;
    protected Owner owner;
    protected Register register;

    public Account() {
    	this("", 0.0, new Owner("NO OWNER"));
    }

    public Account(String name, double balance, Owner owner) {
        this.name = name;
        this.balance = balance;
        this.owner = owner;
        register = new Register();
        register.add("OPEN", balance);
    }

    public void deposit(double amount) throws Exception {
        deposit(amount, "DEP");
    }

    public void deposit(double amount, String registerEntry) {
        logger.info(name + " Depositing " + amount);
        logger.debug(name + " Balance before deposit: " + balance);
        balance += amount;
        logger.debug(name + " Balance after deposit: " + balance);
        register.add(registerEntry, amount);
    }

    public void withdraw(double amount) throws Exception {
        withdraw(amount, "W/D");
    }

    public void withdraw(double amount, String registerEntry) {
    	logger.debug(name + " Before w/d "+ getBalance());
        balance = balance - amount;
        logger.debug(name + " After w/d " + getBalance());
        register.add(registerEntry, amount);
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

    public List<String> generateStatement() {
        monthEnd();

        List<String> rtn = new ArrayList<>();
        List<Map.Entry<String, Double>> registerEntries = register.getEntries();
        for (Map.Entry<String, Double> entry : registerEntries) {
            String val = String.format("%8s: %f", entry.getKey(), entry.getValue());
            rtn.add(val);
        }
        return rtn;
    }

    public String toString() {
        return String.format("%s\tAccount name: %s\tBalance: %s", owner, name, balance);
    }

    abstract public void monthEnd();
}
