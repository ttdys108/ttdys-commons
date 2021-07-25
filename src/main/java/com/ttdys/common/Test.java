package com.ttdys.common;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
public class Test {

    private String name;
    private String code;
    private int type;

    private Date date;

    private BigDecimal amt;

}
