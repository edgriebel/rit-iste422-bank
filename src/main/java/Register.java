import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Register {
    Logger logger = LogManager.getLogger(Register.class.getName());
    List<Map.Entry<String, Double>> entryList = new ArrayList<>();

    public void add(String entryName, Double amount) {
        logger.debug("Register entry adding: " + entryName + ", " + amount);
        entryList.add(new AbstractMap.SimpleEntry<>(entryName, amount));
    }

    public List<Map.Entry<String, Double>> getEntries() {
        return entryList;
    }

}
