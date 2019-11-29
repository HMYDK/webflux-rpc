package com.paranoia.rsocket.annotation;

import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

/**
 * @author ZHANGKAI
 * @date 2019/11/21
 * @description
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(EnableWebFluxRpcClientScanRegistrar.class)
public @interface EnableWebFluxRpcClient {
}
