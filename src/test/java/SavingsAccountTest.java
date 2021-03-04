import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

public class SavingsAccountTest {

	@Test
	public void givenSavingsAccount_whenNoArgConstructorCalled_thenDefaultsSet() {
		// we can test that an empty object is created
		SavingsAccount s = new SavingsAccount();
		assertThat("Balance should be zero", s.getBalance(), is(0.0d));
		assertThat("Name should be empty", s.getName(), is(""));
		assertThat("Rate should be 0", s.getInterestRate(), is(0.0d));
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenSavingsAccount_whenNegativeInterestRate_thenExceptionIsThrown() {
		// Interest rate should never be negative so let's see if an exception is thrown
		SavingsAccount s = new SavingsAccount("name", 0, -1, null);
		// we should never get here!
		System.out.println(s);
	}

}
