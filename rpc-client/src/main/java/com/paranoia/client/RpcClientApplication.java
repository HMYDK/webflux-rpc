package com.paranoia.client;

import com.paranoia.rsocket.annotation.EnableWebFluxRpc;
import com.paranoia.rsocket.client.RpcProxy;
import io.netty.channel.ChannelOption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.netty.tcp.TcpClient;

@SpringBootApplication
/**
 *  支持自定义rpc客户端注解
 */
@EnableWebFluxRpc
public class RpcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcClientApplication.class, args);
    }

    @Bean
    public RpcProxy getRpcProxy() {
        TcpClient tcpClient = TcpClient.create()
                .host("localhost")
                .port(9999)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE);
        return new RpcProxy(tcpClient);
    }

}
