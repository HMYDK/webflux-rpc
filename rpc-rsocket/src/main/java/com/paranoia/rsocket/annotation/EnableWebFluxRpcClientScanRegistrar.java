package com.paranoia.rsocket.annotation;

import com.paranoia.rsocket.server.RsocketProtocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author ZHANGKAI
 * @date 2019/11/28
 * @description
 */
public class EnableWebFluxRpcClientScanRegistrar implements ImportBeanDefinitionRegistrar {

    private final Log logger = LogFactory.getLog(getClass());


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        //注册服务提供者实现的api-service实现类
        registerRpcServerServiceImpls(annotationMetadata);

        //注册@Reference bean
        registerReferenceAnnotationBeanPostProcessor(beanDefinitionRegistry);
    }

    private void registerRpcServerServiceImpls(AnnotationMetadata annotationMetadata) {
        Set<String> packagesToScan = getPackagesToScan(annotationMetadata);

        logger.info("Register rpc-server service packages amount :" + packagesToScan.size());
        if (CollectionUtils.isEmpty(packagesToScan)) {
            return;
        }

        RsocketProtocol rsocketProtocol = new RsocketProtocol();
        rsocketProtocol.registerRpcServerServicePackages(packagesToScan);
        try {
            rsocketProtocol.doRegister();
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("RsocketProtocol.doRegister error");
            }
            e.printStackTrace();
        }
        logger.info("Register rpc-server service class amount :" + rsocketProtocol.getClassCacheSize());
    }

    private void registerReferenceAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {

        if (!registry.containsBeanDefinition(WebFluxRpcReferenceAnnotationBeanPostProcessor.BEAN_NAME)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(WebFluxRpcReferenceAnnotationBeanPostProcessor.class);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(WebFluxRpcReferenceAnnotationBeanPostProcessor.BEAN_NAME, beanDefinition);
        }

    }

    private Set<String> getPackagesToScan(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes annotationAttributes =
                AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableWebFluxRpc.class.getName()));

        if (annotationAttributes == null) {
            return new LinkedHashSet<>();
        }

        String[] rpcServerServicePackages = annotationAttributes.getStringArray("rpcServerServicePackages");

        return new LinkedHashSet<>(Arrays.asList(rpcServerServicePackages));
    }
}
