package com.paranoia.client;

import com.paranoia.rsocket.annotation.EnableWebFluxRpc;
import com.paranoia.rsocket.client.RpcProxy;
import com.paranoia.rsocket.util.TcpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
/**
 *  支持自定义rpc客户端注解
 */
@EnableWebFluxRpc
public class RpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
    }


    @Value("${webflux.rpc.server.host}")
    private String host;

    @Value("${webflux.rpc.server.port}")
    private int port;

    @Bean
    public RpcProxy getRpcProxy() {
        return new RpcProxy(TcpUtils.getTcpClient(host, port));
    }

}
