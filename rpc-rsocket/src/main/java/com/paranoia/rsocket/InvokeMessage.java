package com.paranoia.rsocket;

import java.io.Serializable;

/**
 * @author ZHANGKAI
 * @date 2019/9/18
 * @description : 封装服务调用信息
 */
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


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }
}
