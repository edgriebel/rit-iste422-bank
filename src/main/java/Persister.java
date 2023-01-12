import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Persister {
    public static Logger logger = LogManager.getLogger(Persister.class.getName());
    public static List<Owner> readOwnersFromCsv(final String csvFilename) throws IOException, SerializationException {
        if (! Paths.get(csvFilename).toFile().exists()) {
            logger.info("File {} doesn't exist, skipping load", csvFilename);
            return Collections.emptyList();
        }
        logger.info("Loading owners from file {}", csvFilename);
        List<Owner> owners = new ArrayList<>();
        List<String> ownersCsv = Files.readAllLines(Paths.get(csvFilename));
        for (String ownerCsv : ownersCsv) {
            owners.add(Owner.fromCSV(ownerCsv));
        }
        logger.info("Loaded {} owners from file {}", owners.size(), csvFilename);
        return owners;
    }

    public static List<SavingsAccount> readSavingsAccountsFromCsv(final String csvFilename) throws IOException, SerializationException {
        if (! Paths.get(csvFilename).toFile().exists()) {
            logger.info("File {} doesn't exist, skipping load", csvFilename);
            return Collections.emptyList();
        }
        logger.info("Loading savings accounts from file {}", csvFilename);
        List<SavingsAccount> recs = new ArrayList<>();
        List<String> recsCsv = Files.readAllLines(Paths.get(csvFilename));
        for (String recCsv : recsCsv) {
            recs.add(SavingsAccount.fromCSV(recCsv));
        }
        logger.info("Loaded {} savings accounts from file {}", recs.size(), csvFilename);
        return recs;
    }

    public static List<CheckingAccount> readCheckingAccountsFromCsv(final String csvFilename) throws IOException, SerializationException {
        if (! Paths.get(csvFilename).toFile().exists()) {
            logger.info("File {} doesn't exist, skipping load", csvFilename);
            return Collections.emptyList();
        }
        logger.info("Loading checking accounts from file {}", csvFilename);
        List<CheckingAccount> recs = new ArrayList<>();
        List<String> recsCsv = Files.readAllLines(Paths.get(csvFilename));
        for (String recCsv : recsCsv) {
            recs.add(CheckingAccount.fromCSV(recCsv));
        }
        logger.info("Loaded {} checking accounts from file {}", recs.size(), csvFilename);
        return recs;
    }

    public static <T extends Persistable>int writeRecordsToCsv(final Collection<T> records, final String csvFilename) throws IOException, SerializationException {
        logger.info("Saving {} records to file {}", records.size(), csvFilename);
        List<String> data = new ArrayList<>();
        for (Persistable o : records) {
            data.add(o.toCSV());
        }
        if (Files.exists(Paths.get(csvFilename))) {
            logger.info("Overwriting records in {}", csvFilename);
        }
        Files.write(Paths.get(csvFilename), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        logger.info("Saved {} records to file {}", data.size(), csvFilename);
        return data.size();
    }
}
