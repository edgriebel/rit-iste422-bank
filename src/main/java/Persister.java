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
        List<Owner> recs = new ArrayList<>();
        List<String> recsCsv = Files.readAllLines(Paths.get(csvFilename));
        for (String recCsv : recsCsv) {
            if (recCsv.startsWith(CheckingAccount.COLUMNS[0])) {
                logger.debug("Skipping header");
                continue;
            }
            recs.add(Owner.fromCSV(recCsv));
        }
        logger.info("Loaded {} owners from file {}", recs.size(), csvFilename);
        return recs;
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
            if (recCsv.startsWith(SavingsAccount.COLUMNS[0])) {
                logger.debug("Skipping header");
                continue;
            }
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
            if (recCsv.startsWith(CheckingAccount.COLUMNS[0])) {
                logger.debug("Skipping header");
                continue;
            }
            recs.add(CheckingAccount.fromCSV(recCsv));
        }
        logger.info("Loaded {} checking accounts from file {}", recs.size(), csvFilename);
        return recs;
    }

    public static <T extends Persistable>int writeRecordsToCsv(final Collection<T> records, final String csvFilename) throws IOException, SerializationException {
        List<String> data = new ArrayList<>();
        boolean header = true;
        // if there's no records we still want to write out an empty file
        if (records == null || records.isEmpty()) {
            logger.info("No records, creating empty file");
            header = false;
        } else {
            logger.info("Saving {} records to file {}", records.size(), csvFilename);
            String [] headers = records.stream().findAny().get().columns();
            data.add(String.join(",", headers));
            for (Persistable o : records) {
                data.add(o.toCSV());
            }
        }
        if (Files.exists(Paths.get(csvFilename))) {
            logger.info("Overwriting records in {}", csvFilename);
        }
        Files.write(Paths.get(csvFilename), data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        logger.info("Saved {} records{} to file {}",
                data.size() - (header ? 1 : 0), (header ? " and header" : ""), csvFilename);
        return data.size() - (header ? 1 : 0);
    }
}
