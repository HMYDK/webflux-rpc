package com.paranoia.rpc;

import com.paranoia.rsocket.annotation.EnableWebFluxRpc;
import com.paranoia.rsocket.util.TcpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
@EnableWebFluxRpc(rpcServerServicePackages = {"com.paranoia.rpc.service"})
public class RpcServerApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RpcServerApplication.class, args);

    }

    @Value("${webflux.rpc.server.host}")
    private String host;

    @Value("${webflux.rpc.server.port}")
    private int port;

    @PostConstruct
    private void init() {
        TcpUtils.registerTcpServer(host, port);
    }
}
