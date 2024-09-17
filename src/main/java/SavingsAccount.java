import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class SavingsAccount extends Account {
    public static Logger logger = LogManager.getLogger(SavingsAccount.class);

    public static final String [] COLUMNS = {
            "id", "name", "balance", "interestRate", "ownerId", "version"
    };

    /** interestRate is an annualized fractional value, e.g. 1% interest is 0.01 */
    double interestRate;

    public SavingsAccount() {
        logger.debug("Created no-arg savings account");
        interestRate = 0.0;
    }

    public SavingsAccount(String name, long id, double balance, double interestRate, long ownerId) {
        super(name, id, balance, ownerId);
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
        return "Savings " + super.toString() +
                " Interest Rate: " + (100*interestRate) + "% ";
    }

    public static SavingsAccount fromCSV(String csv) throws SerializationException {
        final String [] fields = csv.split(DELIMITER);
        final String version = fields[fields.length-1].trim();
        if (! version.equals("v1")) {
            throw new SerializationException("Verison incorrect or missing, expected v1 but was " + version);
        }
        if (fields.length != COLUMNS.length) {
            throw new SerializationException(String.format("not enough fields, should be %d but was %d: %s",
                    COLUMNS.length, fields.length, csv));
        }
        return new SavingsAccount(
            // Fields: String name, long id, double balance, double interestRate, Owner owner
            fields[1].trim(),
            Long.parseLong(fields[0].trim()),
            Double.parseDouble(fields[2].trim()),
            Double.parseDouble(fields[3].trim()),
            Long.parseLong(fields[4].trim())
        );
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public String [] columns() {
        return COLUMNS;
    }

    @Override
    public String toCSV() {
        // Fields in object: String name, long id, double balance, double interestRate, long ownerId
        List<String> values = List.of(                getId(),
                        name,
                        getBalance(),
                        interestRate,
                        getOwnerId(),
                        "v1"
                ).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return String.join(DELIMITER+" ", values);
    }
}
