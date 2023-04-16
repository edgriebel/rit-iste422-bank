import static org.junit.Assert.*;

import java.util.List;
import java.util.Map.Entry;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;

import org.junit.Test;

public class AccountTest {
	Account account;
	
	@Before
	public void createAccount() {
		// because Account is an abstract we need to provide an impl for monthEnd
		account = new Account() { 
			@Override 
			public void monthEnd() { 
			} 
		};
	}

	@Test
	public void givenAccountWithZeroBalance_whenDepositTen_thenBalanceIsTen() throws Exception {
		account.deposit(10);
		assertThat("Deposit 10, balance should be 10", account.getBalance(), is(10.0));
	}

	@Test
	public void givenAccountWithZeroBalance_whenDepositTen_thenRegisterShowsTen() throws Exception {
		account.deposit(10);
		List<RegisterEntry> register = account.getRegisterEntries();
		assertThat("Register should have 2 entries, OPEN and DEP: " + register, register.size(), is(2));
		RegisterEntry entry = register.get(1); // 2nd element
		assertThat(entry.entryName(), is("DEP"));
		assertThat(entry.amount(), is(10.0));
	}

}
