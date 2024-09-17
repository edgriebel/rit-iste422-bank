import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
//import com.fasterxml.jackson.databind.ObjectMapper;

public record Owner(String name, long id, Date dob, String ssn, String address, String address2, String city, String state,
             String zip) implements Persistable {
    public static final String[] COLUMNS = {"id", "name", "dob", "ssn", "address", "address2", "city", "state", "zip", "version"};

    public Owner(String name) {
        this(name, new Random().nextLong(), new Date(0), null, null, null, null, null, null);
    }

    public static Owner fromCSV(final String csv) throws SerializationException {
        final String[] fields = csv.split(DELIMITER);
        final String version = fields[fields.length - 1];
        if (!version.trim().equals("v1")) {
            throw new SerializationException("Verison incorrect or missing, expected v1 but was " + version + ". Record:'" + List.of(fields) + "'");
        }
        if (fields.length != COLUMNS.length) {
            throw new SerializationException(String.format("not enough fields, should be %d but was %d: %s", COLUMNS.length, fields.length, csv));
        }
        return new Owner(
                fields[1].trim(),
                Long.parseLong(fields[0].trim()),
                new Date(Long.parseLong(fields[2].trim())),
                fields[3].trim(),
                fields[4].trim(),
                fields[5].trim(),
                fields[6].trim(),
                fields[7].trim(),
                fields[8].trim());
    }

    public String[] columns() {
        return COLUMNS;
    }

    @Override
    public String toCSV() throws SerializationException {
        List<String> values = List.of(id, name, dob.getTime(), (ssn != null) ? ssn : "", (address != null) ? address : "", (address2 != null) ? address2 : "", (city != null) ? city : "", (state != null) ? state : "", (zip != null) ? zip : "", "v1").stream().map(Object::toString).collect(Collectors.toList());
        return String.join(DELIMITER + " ", values);
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Owner owner = (Owner) o;
        return id == owner.id && name.equals(owner.name) && Objects.equals(dob, owner.dob) && Objects.equals(ssn, owner.ssn) && Objects.equals(address, owner.address) && Objects.equals(address2, owner.address2) && Objects.equals(city, owner.city) && Objects.equals(state, owner.state) && Objects.equals(zip, owner.zip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, dob, ssn, address, address2, city, state, zip);
    }
}
