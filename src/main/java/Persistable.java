public interface Persistable {

    public static final String DELIMITER = "!";

    String toCSV() throws SerializationException;
    Long getId();
    String [] columns();
}
