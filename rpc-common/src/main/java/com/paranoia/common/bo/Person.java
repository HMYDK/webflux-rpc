package com.paranoia.common.bo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ZHANGKAI
 * @date 2019/11/14
 * @description
 */
@Data
public class Person implements Serializable {
    /***/
    private String name;

    private int age;

    private BigDecimal money;

    private Address address;
}
