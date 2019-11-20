package com.paranoia.rsocket.client;

import com.paranoia.common.InvokeMessage;
import com.paranoia.rsocket.util.RpcUtils;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author ZHANGKAI
 * @date 2019/9/18
 * @description
 */
public class RpcProxy {

    private TcpClient tcpClient;

    public RpcProxy(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 若调用的是Object的方法，则直接进行本地调用
                        if (Object.class.equals(method.getDeclaringClass())) {
                            return method.invoke(this, args);
                        }
                        // 远程调用发生在这里
                        if (method.getReturnType().getName().equals(Mono.class.getName())) {
                            return monoRpcInvoke(clazz, method, args);
                        }
                        if (method.getReturnType().getName().equals(Flux.class.getName())) {
                            return fluxRpcInvoke(clazz, method, args);
                        }

                        //todo 同步请求的处理
                        return null;
                    }
                }
        );
    }

    private Publisher monoRpcInvoke(Class<?> clazz, Method method, Object[] args) {
        InvokeMessage invokeMessage = getRequestInfo(clazz, method, args);

        return RSocketFactory.connect()
                .transport(TcpClientTransport.create(tcpClient))
                .start()
                .flatMap(rSocket ->
                        rSocket.requestResponse(RpcUtils.convertIntoPayload(invokeMessage))
                                .map(RpcUtils::decodePayload)
                );
    }

    private Publisher fluxRpcInvoke(Class<?> clazz, Method method, Object[] args) {
        InvokeMessage invokeMessage = getRequestInfo(clazz, method, args);

        return RSocketFactory.connect()
                .transport(TcpClientTransport.create(tcpClient))
                .start()
                .flatMapMany(rSocket ->
                        rSocket.requestStream(RpcUtils.convertIntoPayload(invokeMessage))
                                .map(RpcUtils::decodePayload)
                );
    }


    /**
     * 组装client端的请求信息
     *
     * @param clazz  clazz
     * @param method method
     * @param args   args
     * @return InvokeMessage
     */
    private InvokeMessage getRequestInfo(Class<?> clazz, Method method, Object[] args) {
        InvokeMessage message = new InvokeMessage();
        message.setClassName(clazz.getName());
        message.setMethodName(method.getName());
        message.setParamTypes(method.getParameterTypes());
        message.setParamValues(args);
        return message;
    }


}
