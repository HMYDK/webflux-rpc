package com.paranoia.rsocket.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author ZHANGKAI
 * @date 2019/11/28
 * @description
 */
public abstract class ClassUtils {

    public static <T> Class<T> resolveGenericType(Class<?> declaredClass) {
        ParameterizedType parameterizedType = (ParameterizedType) declaredClass.getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[0];
    }
}
