package com.mih.completablefuture;

import java.math.BigDecimal;

public class Transaction {
    private String id;
    private String name;
    private BigDecimal value;

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

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public static Transaction of(String id, String name, BigDecimal value) {
        Transaction t = new Transaction();
        t.id = id;
        t.name = name;
        t.value = value;
        return t;
    }
}
