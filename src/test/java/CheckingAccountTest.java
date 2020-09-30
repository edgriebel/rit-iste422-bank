import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckingAccountTest {
    @Test
    public void testCheck() throws Exception {
        CheckingAccount acct = new CheckingAccount("test", 100, null);
        acct.writeCheck("Target", 100);
        assertEquals("Balance not zero", 0, acct.getBalance(), 0.01);
    }
    
}
