package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

	@Autowired
	private AccountsService accountsService;

	@Test
	public void addAccount() throws Exception {
		Account account = new Account("Id-123");
		account.setBalance(new BigDecimal(1000));
		this.accountsService.createAccount(account);

		assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		String uniqueId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueId);
		this.accountsService.createAccount(account);

		try {
			this.accountsService.createAccount(account);
			fail("Should have failed when adding duplicate account");
		} catch (DuplicateAccountIdException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
		}

	}

	@Test
	public void transferAmount() throws Exception {
		Account fromAccount = new Account("Id-1", new BigDecimal(4569));
		Account toAccount = new Account("Id-4", new BigDecimal(10598));
		this.accountsService.createAccount(fromAccount);
		this.accountsService.createAccount(toAccount);
		BigDecimal amount = new BigDecimal(223);
		BigDecimal debitedAmountFromAccount = fromAccount.getBalance().subtract(amount);
		BigDecimal creditedAmountToAccount = fromAccount.getBalance().add(amount);
		this.accountsService.transferAmount(fromAccount.getAccountId(), toAccount.getAccountId(), amount);
		Account fromAccountAfterDebit = this.accountsService.getAccount(fromAccount.getAccountId());
		Account toAccountAfterCredit = this.accountsService.getAccount(toAccount.getAccountId());
		assertThat(debitedAmountFromAccount.equals(fromAccountAfterDebit.getBalance()));
		assertThat(creditedAmountToAccount.equals(toAccountAfterCredit.getBalance()));
	}

	@Test
	public void transferAmount_failsForfromAccountNotExist() throws Exception {
		Account fromAccount = new Account("Id-2", new BigDecimal(4569));
		Account toAccount = new Account("Id-4", new BigDecimal(10598));
		this.accountsService.createAccount(fromAccount);
		this.accountsService.createAccount(toAccount);
		BigDecimal amount = new BigDecimal(223);
		try {
			this.accountsService.transferAmount("Id-1", toAccount.getAccountId(), amount);
			fail("Failed because from account does not exist");
		} catch (AccountNotExistException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account with id 'Id-1' does not exist!");
		}

	}
	
	@Test
	public void transferAmount_failsForToAccountNotExist() throws Exception {
		Account fromAccount = new Account("Id-2", new BigDecimal(4569));
		Account toAccount = new Account("Id-4", new BigDecimal(10598));
		this.accountsService.createAccount(fromAccount);
		this.accountsService.createAccount(toAccount);
		BigDecimal amount = new BigDecimal(223);
		try {
			this.accountsService.transferAmount(fromAccount.getAccountId(), "Ïd-5", amount);
			fail("Failed because to account does not exist");
		} catch (AccountNotExistException ex) {
			assertThat(ex.getMessage()).isEqualTo("Account with id 'Id-5' does not exist!");
		}

	}
}
