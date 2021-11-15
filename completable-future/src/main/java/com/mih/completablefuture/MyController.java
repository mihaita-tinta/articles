package com.mih.completablefuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
public class MyController {
    private static final Logger log = LoggerFactory.getLogger(MyController.class);
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final BalanceService balanceService;
    private final Executor executor;

    public MyController(AccountService accountService, TransactionService transactionService, Executor executor, BalanceService balanceService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.balanceService = balanceService;
        this.executor = Executors.newFixedThreadPool(1);
    }

    @GetMapping("/api/accounts/details")
    public AccountWithTransactions getAccountsDetails() throws ExecutionException, InterruptedException {
        log.debug("getAccountsDetails - start http");
        return accountService.getAccounts()
                .thenApplyAsync(accounts -> {
                    log.debug("getAccountsDetails - start ");
                    AccountWithTransactions a = new AccountWithTransactions();
                    a.setAccounts(new ArrayList<>());
                    accounts.forEach(account -> {
                        AccountWithTransactions.Accounts newAccount = new AccountWithTransactions.Accounts();
                        newAccount.setAccount(account);
                        try {
                            newAccount.setTransactions(transactionService.getTransactions(account.getId()).get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            throw new IllegalStateException("can't get transactions", e);
                        }
                        try {
                            newAccount.setBalance(balanceService.getBalance(account.getId()).get());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                            throw new IllegalStateException("can't get transactions", e);
                        }
                        a.getAccounts().add(newAccount);
                    });
                    return a;
                })
                .get();
    }

    @GetMapping("/api/accounts/details-async")
    public CompletableFuture<AccountWithTransactions> getAccountsDetailsAsync() {
        return accountService.getAccounts()
                .thenCompose(accounts -> {
                    List<CompletableFuture<AccountWithTransactions.Accounts>> details = accounts.stream()
                            .map(a -> transactionService.getTransactions(a.getId())
                                    .thenCombine(balanceService.getBalance(a.getId()), (transactions, balance)-> {
                                        AccountWithTransactions.Accounts newAccount = new AccountWithTransactions.Accounts();
                                        newAccount.setAccount(a);
                                        newAccount.setTransactions(transactions);
                                        newAccount.setBalance(balance);
                                        return newAccount;
                                    }))
                            .collect(Collectors.toList());
                    return CompletableFuture.allOf(details.toArray(new CompletableFuture[]{}))
                            .thenApply(v -> {
                                List<AccountWithTransactions.Accounts> accountsList = details.stream()
                                        .map(CompletableFuture::join)
                                        .collect(Collectors.toList());
                                AccountWithTransactions a = new AccountWithTransactions();
                                a.setAccounts(accountsList);
                                return a;
                            });
                });
    }

    @GetMapping("/external-accounts")
    public List<Account> get() {
        return IntStream.range(0, 10)
                .mapToObj(i -> Account.of("id-" + i, "name-" + i))
                .collect(Collectors.toList());
    }
}
