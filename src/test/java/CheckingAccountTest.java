import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CheckingAccountTest {
	CheckingAccount acct;
	
	@Before
	public void setup() {
		System.out.println("Creating a CheckingAccount");
        acct = new CheckingAccount("test", 100, null);
	}
	
    @Test
    public void givenCheckingAccountBalance100_whenWrite100Check_thenBalanceIsZero() throws Exception {
        acct.writeCheck("Target", 100);
        assertThat("Balance not zero!", acct.getBalance(), is(0.0));
    }
    
    @Test
    public void testWithdrawal() throws Exception {
    	acct.withdraw(50);
    	assertThat("Balance shold be 50", acct.getBalance(), is(50.0));
    	
    	acct.withdraw(50);
    	assertThat("Balance shold be 0.0", acct.getBalance(), is(0.0));

    }
        
}
