package com.paranoia.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZHANGKAI
 * @date 2019/9/18
 * @description : 封装服务调用信息
 */
@Data
public class InvokeMessage implements Serializable {

    /**
     * 服务名称
     */
    private String className;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法参数列表
     */
    private Class<?>[] paramTypes;

    /**
     * 方法参数值
     */
    private Object[] paramValues;
}
