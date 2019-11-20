package com.paranoia.client.test;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * @author ZHANGKAI
 * @date 2019/9/19
 * @description
 */
public class StreamTest {
    public static void main(String[] args) {
        RSocketFactory.receive()
                .acceptor(new SocketAcceptorImpl())
                .transport(TcpServerTransport.create("localhost", 7000))
                .start()
                .subscribe();

        RSocket socket =
                RSocketFactory.connect()
                        .transport(TcpClientTransport.create("localhost", 7000))
                        .start()
                        .block();

//        socket.requestStream(DefaultPayload.create("Hello"))
//                .map(Payload::getDataUtf8)
//                .doOnNext(System.out::println)
//                .take(10)
//                .then()
//                .doFinally(signalType -> socket.dispose())
//                .then()
//                .block();

        socket.requestResponse(DefaultPayload.create("这是一个Mono请求"))
                .map(Payload::getDataUtf8)
                .doOnNext(System.out::println)
                .then()
                .doFinally(signalType -> socket.dispose())
                .then()
                .block();
    }

    private static class SocketAcceptorImpl implements SocketAcceptor {
        @Override
        public Mono<RSocket> accept(ConnectionSetupPayload setupPayload, RSocket reactiveSocket) {
            return Mono.just(
                    new AbstractRSocket() {
                        @Override
                        public Flux<Payload> requestStream(Payload payload) {
                            String dataUtf8 = payload.getDataUtf8();
                            System.out.println("dataUtf8 = " + dataUtf8);
                            return Flux.interval(Duration.ofMillis(100))
                                    .map(aLong -> DefaultPayload.create("Interval: " + aLong));
                        }

                        @Override
                        public Mono<Payload> requestResponse(Payload payload) {
                            String dataUtf8 = payload.getDataUtf8();
                            System.out.println("dataUtf8 = " + dataUtf8);
                            return Mono.just(DefaultPayload.create("这是请求参数："+dataUtf8));
                        }
                    });
        }
    }
}
