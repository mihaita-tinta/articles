package com.mih.spring.magic;

import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class AccountRepository {

    public List<Account> getAccounts(Profile profile) {
        return IntStream.range(0, 5)
                .mapToObj(i -> new Account("account-id-" + i, "account-" + profile.getName()))
                .collect(Collectors.toList());
    }
}
