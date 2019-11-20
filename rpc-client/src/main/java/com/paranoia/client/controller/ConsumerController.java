package com.paranoia.client.controller;

import com.paranoia.api.service.HelloService;
import com.paranoia.common.bo.Address;
import com.paranoia.common.bo.Person;
import com.paranoia.rsocket.client.RpcProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * @author ZHANGKAI
 * @date 2019/9/19
 * @description
 */
@RestController
public class ConsumerController {

    @Autowired
    RpcProxy rpcProxy;

    @GetMapping("/consumer")
    public String consumerTest(String name, int age) {
        HelloService helloService = rpcProxy.create(HelloService.class);
        return helloService.sayHi(name, age);
    }

    @GetMapping("/reactive/consumer")
    public Mono<String> reactiveConsumerTest(String name, int age) {

        HelloService helloService = rpcProxy.create(HelloService.class);

        return helloService.sayHiReactive(name, age)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just("service error ~");
                });
    }

    @GetMapping("/reactive/consumer/flux")
    public Flux<Integer> reactiveFluxConsumerTest(int num) {

        HelloService helloService = rpcProxy.create(HelloService.class);

        return helloService.fluxRequest(num)
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Flux.just(500);
                });
    }

    @GetMapping("/reactive/consumer/person")
    public Mono<Person> reactiveConsumerPerson(String name) {

        HelloService helloService = rpcProxy.create(HelloService.class);

        Person person = new Person();
        person.setName(name);
        person.setAge(18);
        person.setMoney(new BigDecimal(100));
        Address address = new Address();
        address.setProvince("浙江省");
        address.setCity("杭州市");
        address.setAddress("宝龙广场");
        person.setAddress(address);

        return helloService.getPersonInfo(person);
    }

    @GetMapping("/reactive/consumer/persons")
    public Flux<Person> reactiveConsumerPersons(String name) {

        HelloService helloService = rpcProxy.create(HelloService.class);

        Person person = new Person();
        person.setName(name);
        person.setAge(18);
        person.setMoney(new BigDecimal(100));
        Address address = new Address();
        address.setProvince("浙江省");
        address.setCity("杭州市");
        address.setAddress("宝龙广场");
        person.setAddress(address);

        return helloService.getPersonInfos(person);
    }
}









