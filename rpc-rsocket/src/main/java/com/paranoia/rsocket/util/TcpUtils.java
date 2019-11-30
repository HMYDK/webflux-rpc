package com.paranoia.rsocket.util;

import com.paranoia.rsocket.server.RsocketProtocol;
import io.netty.channel.ChannelOption;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

/**
 * @author PARANOIA_ZK
 * @date 2019/11/30 11:32
 */
public class TcpUtils {

    public static void registerTcpServer(String host, int port) {

        TcpServer tcpServer = TcpServer.create()
                                       .host(host)
                                       .port(port)
                                       .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                                       .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                                       .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                                       .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE);

        RSocketFactory.receive()
                      .acceptor(new RsocketProtocol.SocketAcceptorImpl())
                      .transport(TcpServerTransport.create(tcpServer))
                      .start()
                      .subscribe(closeableChannel -> {
                          //log.info("Initialization RpcServer Succeed");
                          System.out.println("Initialization RpcServer Succeed");
                      });
    }

    public static TcpClient getTcpClient(String host, int port) {
        return TcpClient.create()
                        .host(host)
                        .port(port)
                        .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                        .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                        .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE);

    }
}
