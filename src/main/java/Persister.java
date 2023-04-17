import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Persister {
    public static Logger logger = LogManager.getLogger(Persister.class.getName());
    private static final String DEFAULT_PATH = ".";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_SUFFIX = "";

    private static String persisterPropertiesFile = "persister.properties";

    private static String persistedFileDir;
    private static String persistedFilePrefix;
    private static String persistedFileSuffix;

    protected static String getFilename(String fileType) {
        return String.format("%s/%s%s%s.csv",
                persistedFileDir, fileType, persistedFilePrefix, persistedFileSuffix);
    }

    public static void loadPersistedFileNameAndDir() throws IOException {
        InputStream persisterPropertiesFile = Persister.class.getClassLoader().getResourceAsStream(Persister.persisterPropertiesFile);
//        File propsFile = new File(PERSISTED_FILE_PROPERTIES);
        Properties prop = new Properties();
        if (persisterPropertiesFile != null) { // persisterPropertiesFile.exists()) {
                prop.load(persisterPropertiesFile);
                // get the property value and print it out
                logger.info("File path property:" + prop.getProperty("persisted.path"));
                logger.info("File prefix property:" + prop.getProperty("persisted.prefix"));
                logger.info("File suffix property:" + prop.getProperty("persisted.suffix"));
        } else {
            logger.info("File %s not found in classpath, using default persisted path/suffix...",
                    Persister.persisterPropertiesFile);
        }
        if (persistedFileDir == null)
            persistedFileDir = prop.getProperty("persisted.path", DEFAULT_PATH);
        if (persistedFilePrefix == null)
            persistedFilePrefix = prop.getProperty("persisted.prefix", DEFAULT_PREFIX);
        if (persistedFileSuffix == null)
           persistedFileSuffix = prop.getProperty("persisted.suffix", DEFAULT_SUFFIX);
    }

    public static void resetPersistedFileNameAndDir() {
        persistedFileDir = persistedFilePrefix = persistedFileSuffix = null;
    }

    public static List<Owner> readOwnersFromCsv() throws IOException, SerializationException {
        final String csvFilename = getFilename("owners");
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

    public static List<SavingsAccount> readSavingsAccountsFromCsv() throws IOException, SerializationException {
        final String csvFilename = getFilename("savings");
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

    public static List<CheckingAccount> readCheckingAccountsFromCsv() throws IOException, SerializationException {
        final String csvFilename = getFilename("checking");
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

    public static List<RegisterEntry> readRegisterEntriesFromCsv() throws IOException, SerializationException {
        final String csvFilename = getFilename("register");
        if (! Paths.get(csvFilename).toFile().exists()) {
            logger.info("File {} doesn't exist, skipping load", csvFilename);
            return Collections.emptyList();
        }
        logger.info("Loading RegisterEntries from file {}", csvFilename);
        List<RegisterEntry> recs = new ArrayList<>();
        List<String> recsCsv = Files.readAllLines(Paths.get(csvFilename));
        for (String recCsv : recsCsv) {
            if (recCsv.startsWith(RegisterEntry.COLUMNS[0])) {
                logger.debug("Skipping header");
                continue;
            }
            recs.add(RegisterEntry.fromCSV(recCsv));
        }
        logger.info("Loaded {} register entries from file {}", recs.size(), csvFilename);
        return recs;
    }

    public static <T extends Persistable>int writeRecordsToCsv(final Collection<T> records, final String persistableName) throws IOException, SerializationException {
        List<String> data = new ArrayList<>();
        final String csvFilename = getFilename(persistableName);
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

    public static String getPersisterPropertiesFile() {
        return persisterPropertiesFile;
    }

    public static void setPersisterPropertiesFile(String persisterPropertiesFile) {
        Persister.persisterPropertiesFile = persisterPropertiesFile;
    }

    public static String getPersistedFileDir() {
        return persistedFileDir;
    }

    public static void setPersistedFileDir(String persistedFileDir) {
        Persister.persistedFileDir = persistedFileDir;
        logger.info("Persisted file path set to '{}'", persistedFileDir);
    }

    public static String getPersistedFilePrefix() {
        return persistedFilePrefix;
    }

    public static void setPersistedFilePrefix(String persistedFilePrefix) {
        Persister.persistedFilePrefix = persistedFilePrefix;
        logger.info("Persisted file prefix set to '{}'", persistedFilePrefix);
    }

    public static String getPersistedFileSuffix() {
        return persistedFileSuffix;
    }

    public static void setPersistedFileSuffix(String persistedFileSuffix) {
        Persister.persistedFileSuffix = persistedFileSuffix;
        logger.info("Persisted file suffix set to '{}'", persistedFileSuffix);
    }

}
