package com.mih.spring.magic;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Transaction {

    private String id;
    private String name;
    private BigDecimal value;
}
