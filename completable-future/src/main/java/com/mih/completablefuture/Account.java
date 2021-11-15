package com.mih.completablefuture;

public class Account {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public static Account of(String id, String name) {
        Account account = new Account();
        account.id = id;
        account.name = name;
        return account;
    }
}
