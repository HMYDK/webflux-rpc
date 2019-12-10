package com.paranoia.rsocket.annotation;

import com.paranoia.rsocket.client.RpcProxy;
import com.paranoia.rsocket.server.RsocketProtocol;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author ZHANGKAI
 * @date 2019/11/28
 * @description
 */
public class WebFluxRpcSpringServiceAnnotationBeanPostProcessor
        extends InstantiationAwareBeanPostProcessorAdapter
        implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;

    private Set<String> packagesToScan;

    private RsocketProtocol rsocketProtocol;


    public WebFluxRpcSpringServiceAnnotationBeanPostProcessor(Set<String> packagesToScan, RsocketProtocol rsocketProtocol) {
        this.packagesToScan = packagesToScan;
        this.rsocketProtocol = rsocketProtocol;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.packagesToScan = resolvePackagesToScan(environment, packagesToScan);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (CollectionUtils.isEmpty(packagesToScan)) {
            return bean;
        }
        Class clazz = bean.getClass();
        if (clazz.getAnnotation(Service.class) != null) {
            if (clazz.getAnnotatedInterfaces().length != 0) {
                String apiServicePath = clazz.getAnnotatedInterfaces()[0].getType().getTypeName();
                String apiServicePackagePath = apiServicePath.substring(0, apiServicePath.lastIndexOf("."));
                if (packagesToScan.contains(apiServicePackagePath)) {
                    rsocketProtocol.doRegister(apiServicePath, bean);
                }
            }
        }
        return bean;
    }

    private Set<String> resolvePackagesToScan(Environment environment, Set<String> packages) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<>(packagesToScan.size());
        //Support for lower jdk version
        for (String aPackage : packages) {
            if (StringUtils.hasText(aPackage)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(aPackage.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }
}












