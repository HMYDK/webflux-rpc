package com.paranoia.rsocket.server;

import com.paranoia.common.InvokeMessage;
import com.paranoia.rsocket.util.RpcUtils;
import io.rsocket.*;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ZHANGKAI
 * @date 2019/9/19
 * @description
 */
public class RsocketProtocol {

    /**
     * 存放指定包中所有实现类的类名
     */
    private static List<String> classCache = new ArrayList<>();
    /**
     * 本地服务注册表
     */
    private static Map<String, Object> registerMap = new HashMap<>();

    /**
     * 将指定包下的提供者名称写入到classCache中
     *
     * @param providerPackage com.xx.xxx.service
     */

    public void getProviderClass(String providerPackage) {
        // 将字符串的包转化为了URL对象资源
        URL resource = this.getClass().getClassLoader().getResource(providerPackage.replaceAll("\\.", "/"));
        // 将URL转化为File
        File dir = new File(resource.getFile());
        // 启动指定包的目录，查找.class文件
        for (File file : dir.listFiles()) {
            // 若当前遍历的是目录，则递归
            if (file.isDirectory()) {
                getProviderClass(providerPackage + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                // 获取去掉.class扩展名的简单类名
                String fileName = file.getName().replace(".class", "").trim();
                // 将全限定性类名写入到classCache中
                classCache.add(providerPackage + "." + fileName);
            }
        }
    }

    /**
     * 将服务名称与提供者实例之间的映射关系写入到registerMap
     *
     * @throws Exception 反射触发的异常
     */
    public void doRegister() throws Exception {
        // 若没有提供者类，则无需注册
        if (classCache.size() == 0) {
            return;
        }
        // registerMap的key为接口名，value为该接口对应的实现类的实例
        for (String className : classCache) {
            Class<?> clazz = Class.forName(className);
            registerMap.put(clazz.getInterfaces()[0].getName(), clazz.newInstance());
        }
    }

    /**
     * server socket 处理请求
     */
    public static class SocketAcceptorImpl implements SocketAcceptor {

        @Override
        public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket reactiveSocket) {
            return Mono.just(
                    new AbstractRSocket() {
                        @Override
                        public Flux<Payload> requestStream(Payload payload) {
                            return doFluxRequest(payload);
                        }

                        @Override
                        public Mono<Payload> requestResponse(Payload payload) {
                            return doMonoRequest(payload);
                        }

                    });
        }
    }

    @SuppressWarnings("unchecked")
    private static Flux<Payload> doFluxRequest(Payload payload) {
        InvokeMessage invokeMessage = decodePayload(payload);
        return invoke(invokeMessage)
                .flatMapMany(invoke -> ((Flux<Object>) invoke))
                .map(RpcUtils::convertIntoPayload);
    }

    @SuppressWarnings("unchecked")
    private static Mono<Payload> doMonoRequest(Payload payload) {
        InvokeMessage invokeMessage = decodePayload(payload);
        return invoke(invokeMessage)
                .flatMap(invoke -> ((Mono<Object>) invoke))
                .map(RpcUtils::convertIntoPayload);
    }

    /**
     * 解码payload
     *
     * @param payload payload
     * @return InvokeMessage
     */
    private static InvokeMessage decodePayload(Payload payload) {
        return (InvokeMessage) RpcUtils.decodePayload(payload);
    }

    /**
     * 本地缓存中获取对象实例，进行请求
     *
     * @param invokeMessage invokeMessage
     * @return Mono<Object>
     */
    private static Mono<Object> invoke(InvokeMessage invokeMessage) {
        return Mono.just(new Object())
                .map(invoke -> {
                    if (registerMap.containsKey(invokeMessage.getClassName())) {
                        Object provider = registerMap.get(invokeMessage.getClassName());
                        try {
                            invoke = provider.getClass()
                                    .getMethod(invokeMessage.getMethodName(), invokeMessage.getParamTypes())
                                    .invoke(provider, invokeMessage.getParamValues());
                        } catch (Exception e) {
                            throw Exceptions.propagate(e);
                        }
                    } else {
                        //todo
                    }
                    return invoke;
                });
    }
}






















