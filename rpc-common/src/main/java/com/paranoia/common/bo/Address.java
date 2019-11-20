package com.paranoia.common.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZHANGKAI
 * @date 2019/11/14
 * @description
 */
@Data
public class Address implements Serializable {
    /***/
    private String province;

    /***/
    private String city;

    /***/
    private String address;
}
