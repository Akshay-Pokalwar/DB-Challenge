package com.db.awmd.challenge.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotExistException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountsService {

	private static final String FAILED = "Operation Failed";

	private static final String SUCCESS = "Transfer successfull";

	@Getter
	private AccountsRepository accountsRepository;

//	@Autowired
//	private NotificationService notificationService;

	
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}
	
	public void updateAccountBalance(Account account) {
		this.accountsRepository.updateAccountBalance(account);
	}
	
	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public Map<String, Object> transferAmount(final String fromId, final String toId, final BigDecimal amount) {
		Map<String, Object> resultMap = new HashMap<>();
		
//		********************************
//		Dummy accounts to be created
		Account fromAccountObj = new Account("Id-1", new BigDecimal(4569));
		Account toAccountObj = new Account("Id-3", new BigDecimal(10598));
		createAccount(fromAccountObj);
		createAccount(toAccountObj);
//		********************************
		
		try {
			final Account fromAccount = this.accountsRepository.getAccount(fromId);
			if (fromAccount != null) {
				final Account toAccount = this.accountsRepository.getAccount(toId);
				if (toAccount != null) {
					if (trasferAmountFromOneAccountToOther(fromAccount, toAccount, amount) == 0) {
						resultMap.put("ERROR", FAILED);
						return resultMap;
					}
				} else {
					throw new AccountNotExistException("Account with id '" + toId + "' does not exist!");
				}
			} else {
				throw new AccountNotExistException("Account with id '" + fromId + "' does not exist!");
			}
		} catch (final Exception e) {
			log.info("Error while transfer amount in transferAmount(): {}", e);
			resultMap.put("ERROR", FAILED);
			return resultMap;
		}
		resultMap.put("OK", SUCCESS);
		return resultMap;
	}

	private synchronized int trasferAmountFromOneAccountToOther(Account fromAccount, Account toAccount, BigDecimal amount) {
		try {
			if (fromAccount != null && toAccount != null && fromAccount.getBalance().compareTo(amount) >= 0) {
				Account newFromObject = new Account(fromAccount.getAccountId(), fromAccount.getBalance().subtract(amount));
				Account newToObject = new Account(toAccount.getAccountId(), toAccount.getBalance().add(amount));
//				update newFromObject & newToObject to map
				this.updateAccountBalance(newFromObject);
				this.updateAccountBalance(newToObject);
				
//				********************************
// 				After implementing notifyAboutTransfer in NotificationService below lines can be used
				
//				notificationService.notifyAboutTransfer(newFromObject,
//						"Dear Customer, Your A/C " + newFromObject.getAccountId() + " has a debit by transfer of Rs. "
//								+ amount + " to A/C no. " + toAccount.getAccountId());
//				notificationService.notifyAboutTransfer(newToObject,
//						"Dear Customer, Your A/C " + newToObject.getAccountId() + " is credited by Rs. " + amount
//								+ " by A/C no. " + fromAccount.getAccountId());
				
//				********************************
			}
		} catch (Exception e) {
			log.info(
					"Error while trasfering amount from one account to other in trasferAmountFromOneAccountToOther(): {}",
					e);
			return 0;
		}
		return 1;
	}

	
	
	
}
