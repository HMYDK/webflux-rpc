package com.paranoia.rsocket.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.paranoia.rsocket.util.ClassUtils.resolveGenericType;
import static org.springframework.core.BridgeMethodResolver.findBridgedMethod;
import static org.springframework.core.BridgeMethodResolver.isVisibilityBridgeMethodPair;

/**
 * @author ZHANGKAI
 * @date 2019/11/28
 * @description
 */
public abstract class AbstractAnnotationInjectedBeanPostProcessor<A extends Annotation>
        extends InstantiationAwareBeanPostProcessorAdapter
        implements MergedBeanDefinitionPostProcessor, BeanClassLoaderAware, DisposableBean {

    private final static int CACHE_SIZE = 32;

    private final Log logger = LogFactory.getLog(getClass());

    private final ConcurrentMap<String, AnnotatedInjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(CACHE_SIZE);

    private final ConcurrentMap<String, Object> injectedObjectsCache = new ConcurrentHashMap<>(CACHE_SIZE);

    private final Class<A> annotationType;

    private ClassLoader classLoader;

    AbstractAnnotationInjectedBeanPostProcessor() {
        this.annotationType = resolveGenericType(getClass());
    }


    private Class<A> getAnnotationType() {
        return annotationType;
    }

    @Override
    public PropertyValues postProcessPropertyValues(
            PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeanCreationException {

        //元信息
        AnnotatedInjectionMetadata metadata = findInjectionMetadata(beanName, bean.getClass(), pvs);
        try {
            //注入
            metadata.inject(bean, beanName, pvs);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of @" + getAnnotationType().getName() + " dependencies is failed", ex);
        }
        return pvs;
    }

    private AnnotatedInjectionMetadata findInjectionMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {

        String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());

        AnnotatedInjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized (this.injectionMetadataCache) {
                metadata = this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }
                    try {
                        metadata = buildAnnotatedMetadata(clazz);
                        this.injectionMetadataCache.put(cacheKey, metadata);
                    } catch (NoClassDefFoundError err) {
                        throw new IllegalStateException("Failed to introspect object class [" + clazz.getName() + "] for annotation metadata: could not find class that it depends on", err);
                    }
                }
            }
        }
        return metadata;
    }

    /**
     * 构建注解元信息
     *
     * @param beanClass beanClass
     * @return
     */
    private AnnotatedInjectionMetadata buildAnnotatedMetadata(final Class<?> beanClass) {
        Collection<AnnotatedFieldElement> fieldElements = findFieldAnnotationMetadata(beanClass);
        Collection<AnnotatedMethodElement> methodElements = findAnnotatedMethodMetadata(beanClass);
        return new AbstractAnnotationInjectedBeanPostProcessor.AnnotatedInjectionMetadata(beanClass, fieldElements, methodElements);

    }

    private LinkedList<AnnotatedFieldElement> findFieldAnnotationMetadata(final Class<?> beanClass) {

        final LinkedList<AnnotatedFieldElement> elements = new LinkedList<>();

        ReflectionUtils.doWithFields(beanClass, field -> {

            A annotation = AnnotationUtils.getAnnotation(field, getAnnotationType());

            if (annotation != null) {
                if (Modifier.isStatic(field.getModifiers())) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("@" + getAnnotationType().getName() + " is not supported on static fields: " + field);
                    }
                    return;
                }
                elements.add(new AnnotatedFieldElement(field, annotation));
            }
        });
        return elements;
    }

    private List<AnnotatedMethodElement> findAnnotatedMethodMetadata(final Class<?> beanClass) {

        final List<AnnotatedMethodElement> elements = new LinkedList<AnnotatedMethodElement>();

        ReflectionUtils.doWithMethods(beanClass, method -> {

            Method bridgedMethod = findBridgedMethod(method);

            if (!isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                return;
            }

            A annotation = AnnotationUtils.findAnnotation(bridgedMethod, getAnnotationType());

            if (annotation != null && method.equals(ClassUtils.getMostSpecificMethod(method, beanClass))) {
                if (Modifier.isStatic(method.getModifiers())) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("@" + getAnnotationType().getSimpleName() + " annotation is not supported on static methods: " + method);
                    }
                    return;
                }
                if (method.getParameterTypes().length == 0) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("@" + getAnnotationType().getSimpleName() + " annotation should only be used on methods with parameters: " +
                                method);
                    }
                }
                PropertyDescriptor pd = BeanUtils.findPropertyForMethod(bridgedMethod, beanClass);
                elements.add(new AnnotatedMethodElement(method, pd, annotation));
            }
        });
        return elements;
    }

    private static <T> Collection<T> combine(Collection<? extends T>... elements) {
        List<T> allElements = new ArrayList<T>();
        for (Collection<? extends T> e : elements) {
            allElements.addAll(e);
        }
        return allElements;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata metadata = findInjectionMetadata(beanName, beanType, null);
            metadata.checkConfigMembers(beanDefinition);
        }
    }

    @Override
    public void destroy() throws Exception {

        for (Object object : injectedObjectsCache.values()) {
            if (logger.isInfoEnabled()) {
                logger.info(object + " was destroying!");
            }

            if (object instanceof DisposableBean) {
                ((DisposableBean) object).destroy();
            }
        }

        injectionMetadataCache.clear();
        injectedObjectsCache.clear();

        if (logger.isInfoEnabled()) {
            logger.info(getClass() + " was destroying!");
        }

    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    ClassLoader getClassLoader() {
        return classLoader;
    }


    protected Object getInjectedObject(A annotation, Object bean, String beanName, Class<?> injectedType,
                                       InjectionMetadata.InjectedElement injectedElement) throws Exception {

        return doGetInjectedBean(annotation, bean, beanName, injectedType, injectedElement);

    }


    protected abstract Object doGetInjectedBean(A annotation, Object bean, String beanName, Class<?> injectedType,
                                                InjectionMetadata.InjectedElement injectedElement) throws Exception;


    private class AnnotatedInjectionMetadata extends InjectionMetadata {

        private final Collection<AbstractAnnotationInjectedBeanPostProcessor.AnnotatedFieldElement> fieldElements;

        private final Collection<AbstractAnnotationInjectedBeanPostProcessor.AnnotatedMethodElement> methodElements;

        public AnnotatedInjectionMetadata(Class<?> targetClass, Collection<AbstractAnnotationInjectedBeanPostProcessor.AnnotatedFieldElement> fieldElements,
                                          Collection<AbstractAnnotationInjectedBeanPostProcessor.AnnotatedMethodElement> methodElements) {
            super(targetClass, combine(fieldElements, methodElements));
            this.fieldElements = fieldElements;
            this.methodElements = methodElements;
        }
    }


    private class AnnotatedMethodElement extends InjectionMetadata.InjectedElement {

        private final Method method;

        private final A annotation;


        AnnotatedMethodElement(Method method, PropertyDescriptor pd, A annotation) {
            super(method, pd);
            this.method = method;
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {

            Class<?> injectedType = pd.getPropertyType();

            Object injectedObject = getInjectedObject(annotation, bean, beanName, injectedType, this);

            ReflectionUtils.makeAccessible(method);

            method.invoke(bean, injectedObject);

        }

    }


    public class AnnotatedFieldElement extends InjectionMetadata.InjectedElement {

        private final Field field;

        private final A annotation;

        AnnotatedFieldElement(Field field, A annotation) {
            super(field, null);
            this.field = field;
            this.annotation = annotation;
        }

        @Override
        protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {

            Class<?> injectedType = field.getType();

            Object injectedObject = getInjectedObject(annotation, bean, beanName, injectedType, this);

            ReflectionUtils.makeAccessible(field);

            field.set(bean, injectedObject);

        }

    }

}

