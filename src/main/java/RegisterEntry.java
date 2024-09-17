import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public record RegisterEntry(long id, long accountId, String entryName, double amount, Date date) implements Persistable {
    public static String [] COLUMNS = { "id", "accountId", "entryName", "amount", "date", "version" };
    @Override
    public String toCSV() throws SerializationException {
        List<String> values = List.of(                getId(),
                        accountId,
                        entryName,
                        amount,
                        date.getTime(),
                        "v1"
                ).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return String.join(DELIMITER+" ", values);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String[] columns() {
        return COLUMNS;
    }

    public static RegisterEntry fromCSV(final String csv) throws SerializationException {
        final String[] fields = csv.split(DELIMITER);
        final String version = fields[fields.length - 1];
        if (!version.trim().equals("v1")) {
            throw new SerializationException("Verison incorrect or missing, expected v1 but was " + version);
        }
        if (fields.length != COLUMNS.length) {
            throw new SerializationException(String.format("not enough fields, should be %d but was %d: %s", COLUMNS.length, fields.length, csv));
        }
        return new RegisterEntry(
                Long.parseLong(fields[0].trim()),
                Long.parseLong(fields[1].trim()),
                fields[2].trim(),
                Double.parseDouble(fields[3].trim()),
                new Date(Long.parseLong(fields[4].trim())));
    }

}
