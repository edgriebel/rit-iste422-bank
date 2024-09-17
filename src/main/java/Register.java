import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Register {
    Logger logger = LogManager.getLogger(Register.class.getName());
    private List<RegisterEntry> entryList = new ArrayList<>();
    private static long id = 0;

    public void add(long accountId, String entryName, Double amount, Date transactionDate) {
        logger.debug("Register entry adding: " + entryName + ", " + amount);
        entryList.add(new RegisterEntry(++id, accountId, entryName, amount, transactionDate));
    }

    public void addRegisterEntry(RegisterEntry registerEntry) {
        logger.debug("Adding RegisterEntry verbatim: {}", registerEntry);
        entryList.add(registerEntry);
    }
    public List<RegisterEntry> getEntries() {
        return Collections.unmodifiableList(entryList);
    }

    public List<RegisterEntry> getEntriesForAccount(long accountId) {
        return entryList.stream()
                .filter(e -> e.accountId() == accountId)
                .toList();
    }

    public void clear() {
        entryList.clear();
    }
}
