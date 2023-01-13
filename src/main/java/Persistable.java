public interface Persistable {
    String toCSV() throws SerializationException;
    Long getId();
    String [] columns();
}
