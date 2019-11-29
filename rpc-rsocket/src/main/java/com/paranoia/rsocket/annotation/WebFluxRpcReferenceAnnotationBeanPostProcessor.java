package com.paranoia.rsocket.annotation;

import com.paranoia.rsocket.client.RpcProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author ZHANGKAI
 * @date 2019/11/28
 * @description
 */
public class WebFluxRpcReferenceAnnotationBeanPostProcessor
        extends AbstractAnnotationInjectedBeanPostProcessor<Reference>
        implements ApplicationContextAware {

    static final String BEAN_NAME = "webfluxRpcReferenceAnnotationBeanPostProcessor";

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected Object doGetInjectedBean(Reference annotation, Object bean, String beanName, Class<?> injectedType,
                                       InjectionMetadata.InjectedElement injectedElement) {
        RpcProxy proxy = applicationContext.getBean(RpcProxy.class);
        return proxy.create(injectedType, super.getClassLoader());
    }

}
