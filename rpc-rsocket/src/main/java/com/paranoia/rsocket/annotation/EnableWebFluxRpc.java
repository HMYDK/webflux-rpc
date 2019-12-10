package com.paranoia.rsocket.annotation;

import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author ZHANGKAI
 * @date 2019/11/21
 * @description :
 * 此注解做了两件事情：
 *  1：将rpcServerServicePackages下的services实现类内置本地缓存，用于后续rpc调用
 *  2：consumer controller中所有@Reference标注的service都会生成一个动态代理，所有后续的方法调用全部通过代理发起远程调用
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(EnableWebFluxRpcClientScanRegistrar.class)
public @interface EnableWebFluxRpc {

    /*
     * 服务提供者模块下的 api-service 实现类的包路径
     */
    String[] rpcServerServicePackages() default {};
}
