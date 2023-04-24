import java.util.List;

record Statement(String accountName, double balance, List<String> transactions) {
    @Override
    public String toString() {
        return String.format(
                "Statement{accountName='%s', balance=%,.2f, transactions=%s}",
                accountName, balance, transactions);
    }
}
