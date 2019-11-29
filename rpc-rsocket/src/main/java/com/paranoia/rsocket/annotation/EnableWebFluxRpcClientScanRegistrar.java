package com.paranoia.rsocket.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author ZHANGKAI
 * @date 2019/11/28
 * @description
 */
public class EnableWebFluxRpcClientScanRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        //注册@Reference bean
        registerReferenceAnnotationBeanPostProcessor(beanDefinitionRegistry);
    }

    private void registerReferenceAnnotationBeanPostProcessor(BeanDefinitionRegistry registry) {

        if (!registry.containsBeanDefinition(WebFluxRpcReferenceAnnotationBeanPostProcessor.BEAN_NAME)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(WebFluxRpcReferenceAnnotationBeanPostProcessor.class);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(WebFluxRpcReferenceAnnotationBeanPostProcessor.BEAN_NAME, beanDefinition);
        }

    }
}
