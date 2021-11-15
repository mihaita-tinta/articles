package com.mih.completablefuture;

import java.math.BigDecimal;

public class Balance {
    private String currency;
    private BigDecimal value;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public static Balance of(String currency, BigDecimal value) {
        Balance t = new Balance();
        t.currency = currency;
        t.value = value;
        return t;
    }
}
