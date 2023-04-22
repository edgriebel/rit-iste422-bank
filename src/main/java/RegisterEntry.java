import java.util.Date;

public record RegisterEntry(long id, long accountId, String entryName, double amount, Date date) implements Persistable {
    public static String [] COLUMNS = { "id", "accountId", "entryName", "amount", "date", "version" };
    @Override
    public String toCSV() throws SerializationException {
        return String.format("%d, %d, %s, %f, %d, v1",
                id,
                accountId,
                entryName,
                amount,
                date.getTime());
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
        final String[] fields = csv.split(",");
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
