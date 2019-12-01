package com.paranoia.rsocket.util;

import com.paranoia.rsocket.server.RsocketProtocol;
import io.netty.channel.ChannelOption;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author PARANOIA_ZK
 * @date 2019/11/30 11:32
 */
public class TcpUtils {

    public static void registerTcpServer(String host, int port) {

        try {
            //checkHostAndPort(host, port);
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Register tcp-server failure :" + e.getLocalizedMessage());
        }
    }

    public static TcpClient getTcpClient(String host, int port) {
        TcpClient tcpClient = null;
        try {
            //checkHostAndPort(host, port);
            tcpClient = TcpClient.create()
                                 .host(host)
                                 .port(port)
                                 .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                                 .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                                 .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Create tcp-client failure :" + e.getLocalizedMessage());
        }
        return tcpClient;
    }

    private static void checkHostAndPort(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), 1000);
            if (!socket.isConnected()) {
                throw new RuntimeException("Socket error");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("Incorrect host[%s] and port[%d] or connect timeout", host, port));
        }
    }
}
