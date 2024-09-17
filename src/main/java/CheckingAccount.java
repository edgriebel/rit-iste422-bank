import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class CheckingAccount extends Account {
    public static Logger logger = LogManager.getLogger(CheckingAccount.class.getName());
    public static final String [] COLUMNS = {
        "id", "name", "balance", "checkNumber", "ownerId", "version"
    };

    private long checkNumber;

    public CheckingAccount() {
        logger.debug("Creating no-arg checking account");
        checkNumber = 0;
    }

    public CheckingAccount(String name, long id, double balance, long checkNumber, long ownerId) {
        super(name, id, balance, ownerId);
        logger.debug(String.format("Creating checking account for %d: %s, %f",
               ownerId, name, balance));
        this.checkNumber = checkNumber;
    }

    /**
     * write a check
     *
     * @param name
     * @param amount
     * @return check number
     * @throws Exception if negative balance
     */
    public long writeCheck(String name, double amount, Date transactionDate) throws Exception {
        logger.info(name + " Writing check for " + name + " amount " + amount);
    	logger.debug("Balance before check:" + getBalance() + " check amount: " + amount);
        withdraw(amount, String.format("Check %d", checkNumber), transactionDate);
    	logger.debug("Balance after check:" + getBalance());

        return checkNumber++;
    }

    @Override
    public void monthEnd() {
        if (getBalance() < getMinimumBalance()) {
            withdraw(getBelowMinimumFee(), "MINIMUM BALANCE CHARGE");
        }
        logger.info(name + " Check # at end of month: " + checkNumber);
        register.add(getId(), "END CHECK", (double)checkNumber, new Date());
    }

    public String toString() {
        return "Checking " + super.toString() + " Current Check #" + checkNumber;
    }

    public static CheckingAccount fromCSV(String csv) throws SerializationException {
        final String [] fields = csv.split(DELIMITER);
        final String version = fields[fields.length-1].trim();
        if (! version.equals("v1")) {
            throw new SerializationException("Verison incorrect or missing, expected v1 but was " + version);
        }
        if (fields.length != COLUMNS.length) {
            throw new SerializationException(String.format("not enough fields, should be %d but was %d: %s",
                    COLUMNS.length, fields.length, csv));
        }
        return new CheckingAccount(
                // Fields in object: String name, long id, double balance, long checkNumber, long ownerId
                fields[1].trim(),
                Long.parseLong(fields[0].trim()),
                Double.parseDouble(fields[2].trim()),
                Long.parseLong(fields[3].trim()),
                Long.parseLong(fields[4].trim())
        );
    }

    public String [] columns() {
        return COLUMNS;
    }

    public String toCSV() {
        // Fields: String name, long id, double balance, long checkNumber, long ownerId
        List<String> values = List.of(                getId(),
                name,
                getBalance(),
                checkNumber,
                getOwnerId(),
                "v1"
        ).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return String.join(DELIMITER+" ", values);
    }


}
